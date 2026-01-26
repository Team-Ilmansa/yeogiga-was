package kr.co.yeogiga.infrastructure.config.rabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
    
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * 이메일 발송 작업 수행을 위한 RabbitListenerContainer 생성 팩토리 빈
     *
     * <p> 이메일 발송 로직 성능 측정 데이터 기반 Consumer 할당
     * <p> 산정 공식: cores * (1 + wait_time / service_time)
     *
     * @param connectionFactory RabbitMQ와 연결된 ConnectionFactory
     * @return 설정이 완료된 SimpleRabbitListenerContainerFactory 객체
     */
    @Bean
    public SimpleRabbitListenerContainerFactory emailRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        int processors = Runtime.getRuntime().availableProcessors();

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(processors * 7);
        factory.setMaxConcurrentConsumers(processors * 7 * 2);
        factory.setPrefetchCount(5);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}
