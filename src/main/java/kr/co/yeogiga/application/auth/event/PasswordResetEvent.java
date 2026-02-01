package kr.co.yeogiga.application.auth.event;

import kr.co.yeogiga.domain.event.DomainEvent;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class PasswordResetEvent extends DomainEvent {
    private final String email;
    private final String code;
    private final ZonedDateTime expiredAt;
    
    public PasswordResetEvent(String email, String code, int expiration) {
        super();
        this.email = email;
        this.code = code;
        this.expiredAt = getCreatedAt().plusSeconds(expiration);
    }
}
