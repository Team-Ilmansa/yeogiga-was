package kr.co.yeogiga.domain.outbox.service;

import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import kr.co.yeogiga.domain.outbox.repository.EventOutboxRepository;
import kr.co.yeogiga.domain.outbox.type.EventOutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventOutboxService {
    private final EventOutboxRepository eventOutboxRepository;
    
    private final int MAX_FAIL_COUNT = 3;
    private final int WAITING_EVENT_MIN_AGE_SECONDS = 30;
    private final int WAITING_EVENT_MAX_AGE_MINUTES = 3;
    
    public EventOutbox save(EventOutbox eventOutbox) {
        return eventOutboxRepository.save(eventOutbox);
    }
    
    public List<EventOutbox> readAllPollingEventOutbox() {
        List<EventOutbox> waitingEventOutboxes = eventOutboxRepository.findByStatusAndCreatedAt(
                EventOutboxStatus.WAITING,
                LocalDateTime.now().minusMinutes(WAITING_EVENT_MAX_AGE_MINUTES),
                LocalDateTime.now().minusSeconds(WAITING_EVENT_MIN_AGE_SECONDS)
        );
        
        List<EventOutbox> failedEventOutboxes = eventOutboxRepository.findByStatusAndFailCount(
                EventOutboxStatus.FAILED,
                MAX_FAIL_COUNT
        );
        
        return Stream.concat(waitingEventOutboxes.stream(), failedEventOutboxes.stream()).collect(Collectors.toList());
    }
    
    @Transactional
    public void updateToPublishedByEventIds(List<String> eventIds) {
        eventOutboxRepository.updateStatusPublishedInEventIds(eventIds);
    }
    
    @Transactional
    public void updateToFailedByEventsIds(List<String> eventIds) {
        eventOutboxRepository.updateStatusFailedInEventIds(eventIds);
    }
    
    @Transactional
    public void updateToPublishedByEventId(String eventId) {
        eventOutboxRepository.updateStatusPublishedByEventId(eventId);
    }
    
    @Transactional
    public void updateToFailedByEventId(String eventId) {
        eventOutboxRepository.updateStatusFailedByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public List<Long> findOldPublishedEventIds(LocalDateTime dateTime, long limit) {
        return eventOutboxRepository.findIdsByStatusAndCreatedAtBefore(EventOutboxStatus.PUBLISHED, dateTime, limit);
    }

    @Transactional(readOnly = true)
    public List<Long> findOldFailedEventIds(int failCount, LocalDateTime dateTime, long limit) {
        return eventOutboxRepository.findIdsByStatusAndCreatedAtBeforeAndFailCountGreaterThanEqual(EventOutboxStatus.FAILED, failCount, dateTime, limit);
    }

    @Transactional(readOnly = true)
    public List<Long> findOldWaitingEventIds(LocalDateTime dateTime, long limit) {
        return eventOutboxRepository.findIdsByStatusAndCreatedAtBefore(EventOutboxStatus.WAITING, dateTime, limit);
    }

    @Transactional
    public void deleteByIds(List<Long> ids) {
        if (ids.isEmpty()) return;
        eventOutboxRepository.deleteByIds(ids);
    }
}
