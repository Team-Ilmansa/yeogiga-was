package kr.co.yeogiga.infrastructure.event.publisher;

import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.infrastructure.event.converter.RabbitMQPropertyResolver;
import kr.co.yeogiga.infrastructure.event.dto.EventPublishResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements DomainEventExternalPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQPropertyResolver rabbitMQPropertyResolver;
    
    /**
     * RabbitMQ 메시지브로커로 이벤트를 발행하는 메서드
     *
     * <p> {@code CorrelationData}를 통해 메시지브로커로의 발행 여부를 확인
     *
     * <p> 메시지 발행 중 예외 발생 시 실패 상태 객체를 반환, 성공 시 성공 객체를 반환
     *
     * @param event 도메인 이벤트 객체
     * @return      실행 결과 {@link EventPublishResult}를 감싸고 있는 {@code CompletableFuture} 객체
     */
    @Override
    public CompletableFuture<EventPublishResult> publish(DomainEvent event) {
        CorrelationData correlationData = new CorrelationData(event.getEventId());
        
        CompletableFuture<EventPublishResult> result = correlationData.getFuture().thenApply(confirm -> {
            if (confirm.isAck()) {
                return EventPublishResult.success(event.getEventId());
            } else {
                return EventPublishResult.fail(event.getEventId(), confirm.getReason());
            }
        }).exceptionally(ex -> EventPublishResult.fail(event.getEventId(), ex.getMessage()));
        
        try {
            rabbitTemplate.convertAndSend(
                    rabbitMQPropertyResolver.getPublishExchange(event),
                    rabbitMQPropertyResolver.getPublishRoutingKey(event),
                    event,
                    correlationData
            );
        } catch (Exception e) {
            result.complete(EventPublishResult.fail(event.getEventId(), e.getMessage()));
        }
        
        return result;
    }
    
    /**
     * RabbitMQ 메시지브로커로 문자열 형태의 이벤트를 발행하는 메서드
     *
     * <p> {@code CorrelationData}를 통해 메시지브로커로의 발행 여부를 확인
     *
     * <p>
     *
     * @param eventId   이벤트 식별자(ULID)
     * @param eventType 도메인 이벤트 클래스 타입
     * @param payload   이벤트 객체를 JSON 형태로 변환한 문자열
     * @return          실행 결과 {@link EventPublishResult}를 감싸고 있는 {@code CompletableFuture} 객체
     */
    public CompletableFuture<EventPublishResult> publishRaw(String eventId, String eventType, String payload) {
        CorrelationData correlationData = new CorrelationData(eventId);
        
        CompletableFuture<EventPublishResult> result = correlationData.getFuture().thenApply(confirm -> {
            if (confirm.isAck()) {
                return EventPublishResult.success(eventId);
            } else {
                return EventPublishResult.fail(eventId, confirm.getReason());
            }
        }).exceptionally(ex -> EventPublishResult.fail(eventId, ex.getMessage()));
        
        try {
            Message message = MessageBuilder
                    .withBody(payload.getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            rabbitTemplate.send(
                    rabbitMQPropertyResolver.getPublishExchange(eventType),
                    rabbitMQPropertyResolver.getPublishRoutingKey(eventType),
                    message,
                    correlationData
            );
        } catch (Exception e) {
            result.complete(EventPublishResult.fail(eventId, e.getMessage()));
        }
        
        return result;
    }
}
