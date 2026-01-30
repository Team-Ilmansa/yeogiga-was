package kr.co.yeogiga.infrastructure.event.converter;

import jakarta.annotation.PostConstruct;
import kr.co.yeogiga.application.auth.event.EmailVerificationEvent;
import kr.co.yeogiga.application.auth.event.PasswordResetEvent;
import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.infrastructure.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RabbitMQPropertyResolver {
    private final RabbitMQProperties properties;
    private final Map<String, RabbitMQProperties.Attribute> eventAttributeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        eventAttributeMap.put(PasswordResetEvent.class.getName(), properties.getPasswordReset());
        eventAttributeMap.put(EmailVerificationEvent.class.getName(), properties.getEmailVerification());
    }
    
    public String getPublishExchange(DomainEvent event) {
        return this.getPublishExchange(event.getClass().getName());
    }
    
    public String getPublishExchange(String eventType) {
        RabbitMQProperties.Attribute attribute = eventAttributeMap.get(eventType);
        if (attribute == null) {
            throw new IllegalArgumentException("Unsuppoered event: " + eventType);
        }
        return attribute.getExchange();
    }

    public String getPublishRoutingKey(DomainEvent event) {
        return this.getPublishRoutingKey(event.getClass().getName());
    }

    public String getPublishRoutingKey(String eventType) {
        RabbitMQProperties.Attribute attribute = eventAttributeMap.get(eventType);
        if (attribute == null) {
            throw new IllegalArgumentException("Unsupported event: " + eventType);
        }
        return attribute.getRoutingKey() + ".requested";
    }
}
