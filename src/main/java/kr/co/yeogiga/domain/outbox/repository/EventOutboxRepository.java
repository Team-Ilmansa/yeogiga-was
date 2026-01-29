package kr.co.yeogiga.domain.outbox.repository;

import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EventOutboxRepository extends JpaRepository<EventOutbox, Long>, CustomEventOutboxRepository {
    @Modifying
    @Query(value = "UPDATE event_outbox " +
                   "SET status = 'PUBLISHED' " +
                   "WHERE event_id = :eventId", nativeQuery = true)
    void updateStatusPublishedByEventId(String eventId);
    
    @Modifying
    @Query(value = "UPDATE event_outbox " +
                   "SET status = 'FAILED', " +
                   "    fail_count = fail_count + 1, " +
                   "    last_retried_at = CURRENT_TIMESTAMP " +
                   "WHERE event_id = :eventId", nativeQuery = true)
    void updateStatusFailedByEventId(String eventId);
}
