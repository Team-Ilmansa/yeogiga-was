package kr.co.yeogiga.infrastructure.config.rabbitmq;

import kr.co.yeogiga.infrastructure.properties.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordResetRabbitMQConfig {
    private final RabbitMQProperties.Attribute properties;
    
    public PasswordResetRabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
        this.properties = rabbitMQProperties.getPasswordReset();
    }
    
    @Bean
    public DirectExchange passwordResetExchange() {
        return new DirectExchange(properties.getExchange());
    }
    
    @Bean
    public Queue passwordResetEmailWorkQueue() {
        return QueueBuilder.durable(properties.getQueue() + ".email")
                .withArgument("x-dead-letter-exchange", properties.getExchange() + ".email.retry")
                .withArgument("x-dead-letter-routing-key", properties.getRoutingKey() + ".email.retry")
                .build();
    }
    
    @Bean
    public Binding PasswordResetEmailWorkBinding(Queue passwordResetEmailWorkQueue, DirectExchange passwordResetExchange) {
        return BindingBuilder.bind(passwordResetEmailWorkQueue)
                .to(passwordResetExchange)
                .with(properties.getRoutingKey() + ".email");
    }
    
    @Bean
    public Binding PasswordResetCommonBinding(Queue passwordResetEmailWorkQueue, DirectExchange passwordResetExchange) {
        return BindingBuilder.bind(passwordResetEmailWorkQueue)
                .to(passwordResetExchange)
                .with(properties.getRoutingKey() + ".requested");
    }
    
    @Bean
    public DirectExchange passwordResetEmailRetryExchange() {
        return new DirectExchange(properties.getExchange() + ".email.retry");
    }
    
    @Bean
    public Queue passwordResetEmailRetryQueue() {
        return QueueBuilder.durable(properties.getQueue() + ".email.retry")
                .withArgument("x-message-ttl", properties.getTtl())
                .withArgument("x-dead-letter-exchange", properties.getExchange())
                .withArgument("x-dead-letter-routing-key", properties.getRoutingKey() + ".email")
                .build();
    }
    
    @Bean
    public Binding passwordResetEmailRetryBinding(Queue passwordResetEmailRetryQueue, DirectExchange passwordResetEmailRetryExchange) {
        return BindingBuilder.bind(passwordResetEmailRetryQueue)
                .to(passwordResetEmailRetryExchange)
                .with(properties.getRoutingKey() + ".email.retry");
    }
    
    @Bean
    public DirectExchange passwordResetEmailDeadExchange() {
        return new DirectExchange(properties.getExchange() + ".email.dead");
    }
    
    @Bean
    public Queue passwordResetEmailDeadQueue() {
        return new Queue(properties.getQueue() + ".email.dead");
    }
    
    @Bean
    public Binding passwordResetEmailDeadBinding(Queue passwordResetEmailDeadQueue, DirectExchange passwordResetEmailDeadExchange) {
        return BindingBuilder.bind(passwordResetEmailDeadQueue)
                .to(passwordResetEmailDeadExchange)
                .with(properties.getRoutingKey() + ".email.dead");
    }
}
