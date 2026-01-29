package kr.co.yeogiga.application.event.listener;

import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import kr.co.yeogiga.infrastructure.event.publisher.DomainEventExternalPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.Executor;

import static kr.co.yeogiga.infrastructure.config.AsyncConfig.MESSAGE_PUBLISH_TASK_EXECUTOR;

@Component
@RequiredArgsConstructor
public class DomainEventPublishListener implements DomainEventListener {
    private final DomainEventExternalPublisher eventExternalPublisher;
    private final EventOutboxService eventOutboxService;
    private final Executor messagePublishTaskExecutor;
    
    @Override
    @Async(value = MESSAGE_PUBLISH_TASK_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(DomainEvent event) {
        eventExternalPublisher.publish(event)
                .thenAcceptAsync(result -> {
                    if (result.isSuccess()) {
                        eventOutboxService.updateToPublishedByEventId(result.eventId());
                    } else {
                        eventOutboxService.updateToFailedByEventId(result.eventId());
                    }
                }, messagePublishTaskExecutor);
    }
}
