package kr.co.yeogiga.application.auth.event;

import kr.co.yeogiga.domain.event.DomainEvent;
import lombok.Getter;

@Getter
public class PasswordResetEvent extends DomainEvent {
    private final String email;
    private final String code;
    
    public PasswordResetEvent(String email, String code) {
        super();
        this.email = email;
        this.code = code;
    }
}
