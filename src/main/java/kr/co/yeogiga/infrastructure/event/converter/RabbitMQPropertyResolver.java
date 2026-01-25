package kr.co.yeogiga.infrastructure.event.converter;

import kr.co.yeogiga.application.auth.event.EmailVerificationEvent;
import kr.co.yeogiga.application.auth.event.PasswordResetEvent;
import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.infrastructure.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQPropertyResolver {
    private final RabbitMQProperties properties;
    
    public String getPublishExchange(DomainEvent event) {
        if (event instanceof PasswordResetEvent) {
            return properties.getPasswordReset().getExchange();
        }
        
        if (event instanceof EmailVerificationEvent) {
            return properties.getEmailVerification().getExchange();
        }
        
        throw new IllegalArgumentException("Unsupported event: " + event.getClass().getSimpleName());
    }
    
    public String getPublishRoutingKey(DomainEvent event) {
        if (event instanceof PasswordResetEvent) {
            return properties.getPasswordReset().getRoutingKey() + ".requested";
        }
        
        if (event instanceof EmailVerificationEvent) {
            return properties.getEmailVerification().getExchange() + ".requested";
        }
        
        throw new IllegalArgumentException("Unsupported event: " + event.getClass().getSimpleName());
    }
}
