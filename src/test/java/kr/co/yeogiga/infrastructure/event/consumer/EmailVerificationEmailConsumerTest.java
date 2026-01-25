package kr.co.yeogiga.infrastructure.event.consumer;

import jakarta.mail.MessagingException;
import kr.co.yeogiga.application.auth.event.EmailVerificationEvent;
import kr.co.yeogiga.infrastructure.event.exception.ProcessingFailException;
import kr.co.yeogiga.infrastructure.mail.VerificationCodeEmailSender;
import kr.co.yeogiga.infrastructure.mail.exception.FatalEmailException;
import kr.co.yeogiga.infrastructure.mail.exception.RetryableEmailException;
import kr.co.yeogiga.infrastructure.properties.RabbitMQProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.MailException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationEmailConsumerTest {
    @Mock
    private VerificationCodeEmailSender emailSender;
    
    @Mock
    private RabbitTemplate rabbitTemplate;
    
    @Mock
    private RabbitMQProperties rabbitMQProperties;
    
    @Mock
    private RabbitMQProperties.Attribute emailVerificationProperties;
    
    private EmailVerificationEmailConsumer consumer;
    private final String WORK_QUEUE = "q.email-verification";
    private final String DLX = "x.email-verification";
    private final String DLK = "email-verification";
    
    @BeforeEach
    void setUp() {
        when(rabbitMQProperties.getEmailVerification()).thenReturn(emailVerificationProperties);
        when(emailVerificationProperties.getQueue()).thenReturn(WORK_QUEUE);
        when(emailVerificationProperties.getExchange()).thenReturn(DLX);
        when(emailVerificationProperties.getRoutingKey()).thenReturn(DLK);
        
        consumer = new EmailVerificationEmailConsumer(emailSender, rabbitTemplate, rabbitMQProperties);
    }
    
    @Test
    @DisplayName("성공 - 메시지 수신 후 이메일 발행 성공")
    void success() {
        // given
        EmailVerificationEvent event = new EmailVerificationEvent("test@test.com", "123456");
        
        // when
        consumer.handleMessage(event, null);
        
        // then
        verify(emailSender, times(1)).send("test@test.com", "123456");
    }
    
    @Test
    @DisplayName("실패 - 재시도 가능한 예외가 발생한 경우")
    void failRetry() {
        // given
        EmailVerificationEvent event = new EmailVerificationEvent("test@test.com", "123456");
        
        MailException mockMailException = mock(MailException.class);
        doThrow(new RetryableEmailException("Retryable Exception", mockMailException)).when(emailSender)
                .send("test@test.com", "123456");
        
        // when
        ProcessingFailException exception = assertThrows(ProcessingFailException.class, ()
                -> consumer.handleMessage(event, null));
        
        // then: ProcessingFailException을 반환하여 NACK을 유도
        assertEquals("Retryable Exception", exception.getMessage());
    }
    
    @Test
    @DisplayName("실패 - 재시도 최대 횟수(3회) 초과하여 DLQ로 이동하는 경우")
    void failRetryExceed() {
        // given
        EmailVerificationEvent event = new EmailVerificationEvent("test@test.com", "123456");
        List<Map<String, Object>> xDeath = List.of(Map.of(
                "reason", "rejected",
                "queue", WORK_QUEUE + ".email",
                "count", 2L
        ));
        
        MailException mockMailException = mock(MailException.class);
        doThrow(new RetryableEmailException("Retryable Exception", mockMailException)).when(emailSender)
                .send("test@test.com", "123456");
        
        // when
        consumer.handleMessage(event, xDeath);
        
        // then: DLQ로 메시지를 이동하기 위해 DLX, DLK를 기반으로 메시지를 발행
        verify(rabbitTemplate, times(1)).convertAndSend(eq(DLX + ".email.dead"), eq(DLK + ".email.dead"), eq(event));
    }
    
    @Test
    @DisplayName("실패 - 재시도가 불가능한 예외가 발생한 경우")
    void failFatal() {
        // given
        EmailVerificationEvent event = new EmailVerificationEvent("test@test.com", "123456");
        
        MessagingException mockMessagingException = mock(MessagingException.class);
        doThrow((new FatalEmailException("Fatal Exception", mockMessagingException)))
                .when(emailSender).send("test@test.com", "123456");
                
        // when
        consumer.handleMessage(event, null);
        
        // then: DLQ로 메시지를 이동하기 위해 DLX, DLK를 기반으로 메시지를 발행
        verify(rabbitTemplate, times(1)).convertAndSend(eq(DLX + ".email.dead"), eq(DLK + ".email.dead"), eq(event));
    }
}
