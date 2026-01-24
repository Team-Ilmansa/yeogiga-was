package kr.co.yeogiga.infrastructure.event.publisher;

import kr.co.yeogiga.application.auth.event.PasswordResetEvent;
import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.infrastructure.event.converter.RabbitMQPropertyResolver;
import kr.co.yeogiga.infrastructure.event.dto.EventPublishResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RabbitMQEventPublisherTest {
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @Mock
    private RabbitMQPropertyResolver rabbitMQPropertyResolver;
    
    @InjectMocks
    private RabbitMQEventPublisher rabbitMQEventPublisher;
    
    private DomainEvent event = new PasswordResetEvent("test@test.com", "123456");
    private String eventId = "01KFQ007TN82P4155GX1X0T6SJ";
    
    @Captor
    private ArgumentCaptor<CorrelationData> captor;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(event, "eventId", eventId);
    }
    
    
    @Test
    @DisplayName("성공 - 메시지 발행 후 ACK 수신")
    void success() throws ExecutionException, InterruptedException {
        // given
        String exchange = "x.test";
        String routingKey = "test";
        when(rabbitMQPropertyResolver.getPublishExchange(event)).thenReturn(exchange);
        when(rabbitMQPropertyResolver.getPublishRoutingKey(event)).thenReturn(routingKey);
        
        // when
        CompletableFuture<EventPublishResult> result = rabbitMQEventPublisher.publish(event);
        
        verify(rabbitTemplate).convertAndSend(eq(exchange), eq(routingKey), eq(event), captor.capture());
        
        // CorrelationData를 캡쳐해 성공 상태의 Confirm을 설정하여 완료 처리
        CorrelationData correlationData = captor.getValue();
        CorrelationData.Confirm confirm = new CorrelationData.Confirm(true, null);
        correlationData.getFuture().complete(confirm);
        
        // then
        EventPublishResult eventPublishResult = result.get();
        assertTrue(eventPublishResult.isSuccess());
        assertThat(eventPublishResult.eventId()).isEqualTo(eventId);
    }
    
    @Test
    @DisplayName("실패 - 메시지 발행 후 NACK 수신")
    void failNack() throws ExecutionException, InterruptedException {
        // given
        String exchange = "x.test";
        String routingKey = "test";
        when(rabbitMQPropertyResolver.getPublishExchange(event)).thenReturn(exchange);
        when(rabbitMQPropertyResolver.getPublishRoutingKey(event)).thenReturn(routingKey);
        
        // when
        CompletableFuture<EventPublishResult> result = rabbitMQEventPublisher.publish(event);
        
        verify(rabbitTemplate).convertAndSend(eq(exchange), eq(routingKey), eq(event), captor.capture());
        
        // CorrelationData를 캡쳐해 성공 상태의 Confirm을 설정하여 완료 처리
        CorrelationData correlationData = captor.getValue();
        CorrelationData.Confirm confirm = new CorrelationData.Confirm(false, "NACK");
        correlationData.getFuture().complete(confirm);
        
        // then
        EventPublishResult eventPublishResult = result.get();
        assertFalse(eventPublishResult.isSuccess());
        assertThat(eventPublishResult.eventId()).isEqualTo(eventId);
        assertEquals(eventPublishResult.cause(), "NACK");
    }
    
    @Test
    @DisplayName("실패 - RabbitTemplate 발행 실패")
    void failRabbitTemplate() throws ExecutionException, InterruptedException {
        // given
        String exchange = "x.test";
        String routingKey = "test";
        when(rabbitMQPropertyResolver.getPublishExchange(event)).thenReturn(exchange);
        when(rabbitMQPropertyResolver.getPublishRoutingKey(event)).thenReturn(routingKey);
        doThrow(new RuntimeException("Unexpected Exception")).when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(DomainEvent.class), any(CorrelationData.class));
        
        // when
        CompletableFuture<EventPublishResult> result = rabbitMQEventPublisher.publish(event);
        
        // then
        EventPublishResult eventPublishResult = result.get();
        assertFalse(eventPublishResult.isSuccess());
        assertThat(eventPublishResult.eventId()).isEqualTo(eventId);
        assertEquals(eventPublishResult.cause(), "Unexpected Exception");
    }
}
