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
public class EmailVerificationRabbitMQConfig {
    private final RabbitMQProperties.Attribute properties;
    
    public EmailVerificationRabbitMQConfig(RabbitMQProperties rabbitMQProperties) {
        this.properties = rabbitMQProperties.getEmailVerification();
    }
    
    @Bean
    public DirectExchange emailVerificationExchange() {
        return new DirectExchange(properties.getExchange());
    }
    
    @Bean
    public Queue emailVerificationEmailWorkQueue() {
        return QueueBuilder.durable(properties.getQueue() + ".email")
                .withArgument("x-dead-letter-exchange", properties.getExchange() + ".email.retry")
                .withArgument("x-dead-letter-routing-key", properties.getRoutingKey() + ".email.retry")
                .build();
    }
    
    @Bean
    public Binding emailVerificationEmailWorkBinding(Queue emailVerificationEmailWorkQueue, DirectExchange emailVerificationExchange) {
        return BindingBuilder.bind(emailVerificationEmailWorkQueue)
                .to(emailVerificationExchange)
                .with(properties.getRoutingKey() + ".email");
    }
    
    @Bean
    public Binding emailVerificationCommonBinding(Queue emailVerificationEmailWorkQueue, DirectExchange emailVerificationExchange) {
        return BindingBuilder.bind(emailVerificationEmailWorkQueue)
                .to(emailVerificationExchange)
                .with(properties.getRoutingKey() + ".requested");
    }
    
    @Bean
    public DirectExchange emailVerificationEmailRetryExchange() {
        return new DirectExchange(properties.getExchange() + ".email.retry");
    }
    
    @Bean
    public Queue emailVerificationEmailRetryQueue() {
        return QueueBuilder.durable(properties.getQueue() + ".email.retry")
                .withArgument("x-message-ttl", properties.getTtl())
                .withArgument("x-dead-letter-exchange", properties.getExchange())
                .withArgument("x-dead-letter-routing-key", properties.getRoutingKey() + ".email")
                .build();
    }
    
    @Bean
    public Binding emailVerificationRetryBinding(Queue emailVerificationEmailRetryQueue, DirectExchange emailVerificationEmailRetryExchange) {
        return BindingBuilder.bind(emailVerificationEmailRetryQueue)
                .to(emailVerificationEmailRetryExchange)
                .with(properties.getRoutingKey() + ".email.retry");
    }
    
    @Bean
    public DirectExchange emailVerificationEmailDeadExchange() {
        return new DirectExchange(properties.getExchange() + ".email.dead");
    }
    
    @Bean
    public Queue emailVerificationEmailDeadQueue() {
        return new Queue(properties.getQueue() + ".email.dead");
    }
    
    @Bean
    public Binding emailVerificationDeadBinding(Queue emailVerificationEmailDeadQueue, DirectExchange emailVerificationEmailDeadExchange) {
        return BindingBuilder.bind(emailVerificationEmailDeadQueue)
                .to(emailVerificationEmailDeadExchange)
                .with(properties.getRoutingKey() + ".email.dead");
    }
}
