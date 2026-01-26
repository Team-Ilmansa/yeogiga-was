package kr.co.yeogiga.domain.outbox.service;

import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import kr.co.yeogiga.domain.outbox.repository.EventOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventOutboxService {
    private final EventOutboxRepository eventOutboxRepository;
    
    public EventOutbox save(EventOutbox eventOutbox) {
        return eventOutboxRepository.save(eventOutbox);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToDoneByEventId(String eventId) {
        eventOutboxRepository.updateStatusPublishedByEventId(eventId);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToFailedByEventId(String eventId) {
        eventOutboxRepository.updateStatusFailedByEventId(eventId);
    }
}
