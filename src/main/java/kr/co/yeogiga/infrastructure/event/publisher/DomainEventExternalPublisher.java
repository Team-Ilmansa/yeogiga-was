package kr.co.yeogiga.infrastructure.event.publisher;

import kr.co.yeogiga.infrastructure.event.dto.EventPublishResult;
import kr.co.yeogiga.domain.event.DomainEvent;

import java.util.concurrent.CompletableFuture;

public interface DomainEventExternalPublisher {
    CompletableFuture<EventPublishResult> publish(DomainEvent event);
}
