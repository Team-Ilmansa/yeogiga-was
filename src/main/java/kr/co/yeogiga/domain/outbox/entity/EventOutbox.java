package kr.co.yeogiga.domain.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.domain.outbox.type.EventOutboxStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "event_outbox")
public class EventOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_id", nullable = false, length = 26)
    private String eventId;
    
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @Column(nullable = false)
    private String payload;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventOutboxStatus status;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_retried_at")
    private LocalDateTime lastRetriedAt;
    
    @Column(name = "fail_count")
    private int failCount;
    
    @Builder
    public EventOutbox(String eventId, String eventType, String payload, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
        this.status = EventOutboxStatus.WAITING;
    }
    
    public static EventOutbox fromEvent(DomainEvent event, String payload) {
        return EventOutbox.builder()
                .eventId(event.getEventId())
                .eventType(event.getClass().getName())
                .payload(payload)
                .createdAt(event.getCreatedAt())
                .build();
    }
}

