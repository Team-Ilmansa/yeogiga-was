package kr.co.yeogiga.infrastructure.event.consumer.support;

import kr.co.yeogiga.domain.event.DomainEvent;

import java.time.Duration;

public interface ProcessedEventStore {
    boolean isProcessed(String consumerId, DomainEvent event);
    void markProcessed(String consumerId, DomainEvent event, Duration duration);
}
