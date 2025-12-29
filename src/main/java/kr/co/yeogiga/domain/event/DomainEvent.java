package kr.co.yeogiga.domain.event;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class DomainEvent {
    private final LocalDateTime createdAt;
    private final String eventId;
    
    public DomainEvent() {
        this.createdAt = LocalDateTime.now();
        this.eventId = UlidCreator.getUlid().toString();
    }
}
