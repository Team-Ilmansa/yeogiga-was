package kr.co.yeogiga.domain.event;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.Getter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
public abstract class DomainEvent {
    private final ZonedDateTime createdAt;
    private final String eventId;
    
    public DomainEvent() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        this.eventId = UlidCreator.getUlid().toString();
    }
}
