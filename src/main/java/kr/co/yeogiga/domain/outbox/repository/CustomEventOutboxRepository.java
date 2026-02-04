package kr.co.yeogiga.domain.outbox.repository;

import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import kr.co.yeogiga.domain.outbox.type.EventOutboxStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomEventOutboxRepository {
    List<EventOutbox> findByStatusAndCreatedAt(EventOutboxStatus status, LocalDateTime start, LocalDateTime end);
    List<EventOutbox> findByStatusAndFailCount(EventOutboxStatus status, int failCount);
    List<Long> findIdsByStatusAndCreatedAtBefore(EventOutboxStatus status, LocalDateTime dateTime, long limit);
    List<Long> findIdsByStatusAndCreatedAtBeforeAndFailCountGreaterThanEqual(EventOutboxStatus status, int failCount, LocalDateTime dateTime, long limit);
    void updateStatusPublishedInEventIds(List<String> eventIds);
    void updateStatusFailedInEventIds(List<String> eventIds);
    void deleteByIds(List<Long> ids);
}
