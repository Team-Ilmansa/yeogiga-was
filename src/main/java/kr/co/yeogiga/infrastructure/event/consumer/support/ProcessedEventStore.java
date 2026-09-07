package kr.co.yeogiga.infrastructure.event.consumer.support;

import kr.co.yeogiga.domain.event.DomainEvent;

import java.time.Duration;

public interface ProcessedEventStore {
    boolean isProcessed(DomainEvent event);
    void markProcessed(DomainEvent event, Duration duration);
}
