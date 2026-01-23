package kr.co.yeogiga.application.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import kr.co.yeogiga.domain.outbox.service.EventOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DomainEventRecordListener implements DomainEventListener {
    private final EventOutboxService eventOutboxService;
    private final ObjectMapper objectMapper;
    
    @Override
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleEvent(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            
            eventOutboxService.save(EventOutbox.fromEvent(event, payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse Event - " + event.getEventId(), e);
        }
    }
}
