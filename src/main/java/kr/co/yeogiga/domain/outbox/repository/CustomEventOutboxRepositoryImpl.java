package kr.co.yeogiga.domain.outbox.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.yeogiga.domain.outbox.entity.EventOutbox;
import kr.co.yeogiga.domain.outbox.entity.QEventOutbox;
import kr.co.yeogiga.domain.outbox.type.EventOutboxStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CustomEventOutboxRepositoryImpl implements CustomEventOutboxRepository {
    private final JPAQueryFactory jpaQueryFactory;
    
    private final QEventOutbox eventOutbox = QEventOutbox.eventOutbox;
    
    @Override
    public List<EventOutbox> findByStatusAndCreatedAt(EventOutboxStatus status, LocalDateTime start, LocalDateTime end) {
        return jpaQueryFactory
                .selectFrom(eventOutbox)
                .where(
                        eventOutbox.status.eq(status),
                        createdAtBetween(start, end)
                )
                .fetch();
    }
    
    @Override
    public List<EventOutbox> findByStatusAndFailCount(EventOutboxStatus status, int failCount) {
        return jpaQueryFactory
                .selectFrom(eventOutbox)
                .where(
                        eventOutbox.status.eq(status),
                        eventOutbox.failCount.lt(failCount)
                )
                .fetch();
    }
    
    @Override
    public List<Long> findIdsByStatusAndCreatedAtBefore(EventOutboxStatus status, LocalDateTime dateTime, long limit) {
        return jpaQueryFactory
                .select(eventOutbox.id)
                .from(eventOutbox)
                .where(
                        eventOutbox.status.eq(status),
                        eventOutbox.createdAt.before(dateTime)
                )
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Long> findIdsByStatusAndCreatedAtBeforeAndFailCountGreaterThanEqual(EventOutboxStatus status, int failCount, LocalDateTime dateTime, long limit) {
        return jpaQueryFactory
                .select(eventOutbox.id)
                .from(eventOutbox)
                .where(
                        eventOutbox.status.eq(status),
                        eventOutbox.failCount.goe(failCount),
                        eventOutbox.createdAt.before(dateTime)
                )
                .limit(limit)
                .fetch();
    }
    
    @Override
    public void updateStatusPublishedInEventIds(List<String> eventIds) {
        if (eventIds.isEmpty()) return;
        
        jpaQueryFactory
                .update(eventOutbox)
                .set(eventOutbox.status, EventOutboxStatus.PUBLISHED)
                .where(eventOutbox.eventId.in(eventIds))
                .execute();
    }
    
    @Override
    public void updateStatusFailedInEventIds(List<String> eventIds) {
        if (eventIds.isEmpty()) return;
        
        jpaQueryFactory
                .update(eventOutbox)
                .set(eventOutbox.status, EventOutboxStatus.FAILED)
                .set(eventOutbox.lastRetriedAt, LocalDateTime.now())
                .where(eventOutbox.eventId.in(eventIds))
                .execute();
    }
    
    @Override
    public void deleteByIds(List<Long> ids) {
        jpaQueryFactory
                .delete(eventOutbox)
                .where(eventOutbox.id.in(ids))
                .execute();
    }
    
    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return null;
        if (start == null) return eventOutbox.createdAt.loe(end);
        if (end == null) return eventOutbox.createdAt.goe(start);
        return eventOutbox.createdAt.between(start, end);
    }
}
