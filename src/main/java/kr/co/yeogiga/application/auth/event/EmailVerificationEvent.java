package kr.co.yeogiga.application.auth.event;

import kr.co.yeogiga.domain.event.DomainEvent;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class EmailVerificationEvent extends DomainEvent {
    private final String email;
    private final String code;
    private final ZonedDateTime expiredAt;
    
    public EmailVerificationEvent(String email, String code) {
        super();
        this.email = email;
        this.code = code;
        this.expiredAt = getCreatedAt().plusMinutes(3);
    }
}
