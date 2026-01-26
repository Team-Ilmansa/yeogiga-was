package kr.co.yeogiga.infrastructure.event.consumer;

import kr.co.yeogiga.application.auth.event.PasswordResetEvent;
import kr.co.yeogiga.infrastructure.event.exception.ProcessingFailException;
import kr.co.yeogiga.infrastructure.mail.PasswordResetEmailSender;
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
public class PasswordResetEmailConsumer extends AbstractRabbitEventConsumer<PasswordResetEvent> {
    private final PasswordResetEmailSender passwordResetEmailSender;
    private final RabbitTemplate rabbitTemplate;
    
    private final String WORK_QUEUE;
    private final String DEAD_LETTER_EXCHANGE;
    private final String DEAD_LETTER_ROUTING_KEY;
    private final int MAX_RETRY_COUNT = 3;
    
    public PasswordResetEmailConsumer(PasswordResetEmailSender passwordResetEmailSender, RabbitTemplate rabbitTemplate, RabbitMQProperties rabbitMQProperties) {
        this.passwordResetEmailSender = passwordResetEmailSender;
        this.rabbitTemplate = rabbitTemplate;
        this.WORK_QUEUE = rabbitMQProperties.getPasswordReset().getQueue() + ".email";
        this.DEAD_LETTER_EXCHANGE = rabbitMQProperties.getPasswordReset().getExchange() + ".email.dead";
        this.DEAD_LETTER_ROUTING_KEY = rabbitMQProperties.getPasswordReset().getRoutingKey() + ".email.dead";
    }
    
    @RabbitListener(queues = "#{passwordResetRabbitMQConfig.passwordResetEmailWorkQueue.name}", containerFactory = "emailRabbitListenerContainerFactory")
    public void handleMessage(@Payload PasswordResetEvent event, @Header(name = "x-death", required = false) List<Map<String, Object>> xDeath) {
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
    protected void process(PasswordResetEvent event) {
        passwordResetEmailSender.send(event.getEmail(), event.getCode());
    }
    
    @Override
    protected void retry(PasswordResetEvent event, RuntimeException e, int deathCount) {
        log.warn("[Retry.{}] Message \"{}\" processing failed - {}", deathCount, event.getEventId(), e.getMessage());
        throw new ProcessingFailException(e.getMessage());
    }
    
    @Override
    protected void dead(PasswordResetEvent event, RuntimeException e) {
        log.error("[DEAD-LETTER] Message \"{}\" moved to DLQ. = {}", event.getEventId(), e.getMessage());
        rabbitTemplate.convertAndSend(
                DEAD_LETTER_EXCHANGE,
                DEAD_LETTER_ROUTING_KEY,
                event
        );
    }
}
