package kr.co.yeogiga.infrastructure.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("rabbitmq")
public class RabbitMQProperties {
    private Attribute passwordReset;
    private Attribute emailVerification;

    @Getter
    @RequiredArgsConstructor
    public static class Attribute {
        private final String queue;
        private final String exchange;
        private final String routingKey;
        private final Long ttl;
    }
}
