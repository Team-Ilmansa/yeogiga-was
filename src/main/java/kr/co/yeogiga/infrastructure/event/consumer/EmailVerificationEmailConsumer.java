package kr.co.yeogiga.infrastructure.event.consumer;

import kr.co.yeogiga.application.auth.event.EmailVerificationEvent;
import kr.co.yeogiga.infrastructure.event.exception.ProcessingFailException;
import kr.co.yeogiga.infrastructure.mail.VerificationCodeEmailSender;
import kr.co.yeogiga.infrastructure.properties.RabbitMQProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EmailVerificationEmailConsumer extends AbstractRabbitEventConsumer<EmailVerificationEvent> {
    private final VerificationCodeEmailSender verificationCodeEmailSender;
    private final RabbitTemplate rabbitTemplate;
    
    private final String WORK_QUEUE;
    private final String DEAD_LETTER_EXCHANGE;
    private final String DEAD_LETTER_ROUTING_KEY;
    private final int MAX_RETRY_COUNT = 3;
    
    public EmailVerificationEmailConsumer(VerificationCodeEmailSender verificationCodeEmailSender, RabbitTemplate rabbitTemplate, RabbitMQProperties rabbitMQProperties) {
        this.verificationCodeEmailSender = verificationCodeEmailSender;
        this.rabbitTemplate = rabbitTemplate;
        this.WORK_QUEUE = rabbitMQProperties.getEmailVerification().getQueue() + ".email";
        this.DEAD_LETTER_EXCHANGE = rabbitMQProperties.getEmailVerification().getExchange() + ".email.dead";
        this.DEAD_LETTER_ROUTING_KEY = rabbitMQProperties.getEmailVerification().getRoutingKey() + ".email.dead";
    }
    
    @RabbitListener(queues = "#{emailVerificationRabbitMQConfig.emailVerificationEmailWorkQueue.name}", containerFactory = "emailRabbitListenerContainerFactory")
    public void handleMessage(@Payload EmailVerificationEvent event, @Header(name = "x-death", required = false) List<Map<String, Object>> xDeath) {
        super.handleEvent(event, xDeath);
    }
    
    @Override
    protected String getWorkQueueName() {
        return this.WORK_QUEUE;
    }
    
    @Override
    protected int getMaxRetryCount() {
        return this.MAX_RETRY_COUNT;
    }
    
    @Override
    protected void process(EmailVerificationEvent event) {
        verificationCodeEmailSender.send(event.getEmail(), event.getCode());
    }
    
    @Override
    protected void retry(EmailVerificationEvent event, RuntimeException e, int deathCount) {
        log.warn("[Retry/{}] Message \"{}\" processing failed. - {}", deathCount, event.getEventId(), e.getMessage());
        throw new ProcessingFailException(e.getMessage());
    }
    
    @Override
    protected void dead(EmailVerificationEvent event, RuntimeException e) {
        log.error("[DEAD-LETTER] Message \"{}\" moved to DLQ. = {}", event.getEventId(), e.getMessage());
        rabbitTemplate.convertAndSend(
                DEAD_LETTER_EXCHANGE,
                DEAD_LETTER_ROUTING_KEY,
                event
        );
    }
}
