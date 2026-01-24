package kr.co.yeogiga.infrastructure.event.publisher;

import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.infrastructure.event.converter.RabbitMQPropertyResolver;
import kr.co.yeogiga.infrastructure.event.dto.EventPublishResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

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
}
