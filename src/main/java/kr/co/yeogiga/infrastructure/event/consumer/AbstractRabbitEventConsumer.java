package kr.co.yeogiga.infrastructure.event.consumer;

import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.domain.event.ExpirableEvent;
import kr.co.yeogiga.infrastructure.event.consumer.support.ProcessedEventStore;
import kr.co.yeogiga.infrastructure.event.exception.RetryableException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractRabbitEventConsumer<T extends DomainEvent> {
    private final ProcessedEventStore processedEventStore;
    
    private static final String DEATH_REASON_KEY = "reason";
    private static final String DEATH_QUEUE_KEY = "queue";
    private static final String DEATH_COUNT_KEY = "count";
    private static final String DEATH_REASON_VALUE = "rejected";
    private static final String ZONE = "Asia/Seoul";
    private static final Duration DEFAULT_NON_EXPIRABLE_EVENT_DURATION = Duration.ofDays(1);
    
    public AbstractRabbitEventConsumer(ProcessedEventStore processedEventStore) {
        this.processedEventStore = processedEventStore;
    }
    
    /**
     * 메인으로 메시지를 소비하는 큐의 이름을 반환하는 메서드
     *
     * @return 메인 메시지큐 이름
     */
    protected abstract String getWorkQueueName();
    
    /**
     * 최대 재시도 횟수를 반환하는 메서드
     *
     * @return 최대 재시도 횟수
     */
    protected abstract int getMaxRetryCount();
    
    /**
     * 이벤트에 대한 실제 비즈니스 처리 메서드
     *
     * @param event 수신된 도메인 이벤트
     */
    protected abstract void process(T event);
    
    /**
     * 재시도 처리 메서드
     *
     * <p> 재시도가 가능한 예외 발생 시 호출되어, 재시도 처리 로직을 수행
     *
     * @param event 수신된 도메인 이벤트
     * @param e 발생 예외
     */
    protected abstract void retry(T event, RuntimeException e, int deathCount);
    
    /**
     * Dead Letter 처리 메서드
     *
     * <p> 재시도가 불가능한 예외 발생 또는 최대 재시도 횟수 초과 시, Dead Letter 처리 로직을 수행
     *
     * @param event 수신된 도메인 이벤트
     * @param e 발생 예외
     */
    protected abstract void dead(T event, RuntimeException e);
    
    /**
     * 수신된 이벤트를 처리하고 실패 시 재시도 전략에 따른 제어 메서드
     *
     * <p> 해당 이벤트가 만료 기한이 존재하는 {@link ExpirableEvent}이고, 만료 기한이 지난 경우 처리를 진행하지 않는다.
     *
     * <p> {@link ProcessedEventStore#isProcessed(DomainEvent)}를 호출하여 해당 이벤트가 이미 처리된 경우에는 멱등성을 위하여 처리를 진행하지 않는다.
     *
     * <p> 비즈니스 로직 실행 중 예외 발생 시, 해당 예외에 대한 재시도 가능 여부를 확인한다.
     * <p> 재시도가 가능한 경우, 'x-death' 헤더를 분석하여 현재 재시도 횟수가 최대 허용치({@code getMaxRetryCount()}를 초과했는지 확인한다.
     * <p> 최대 재시도 횟수를 초과했거나 재시도가 불가능한 예외가 발생한 경우, 해당 메시지를 Dead Letter 처리한다.
     *
     * <p> 이벤트 처리에 성공한 경우 해당 이벤트는 처리 완료 여부를 기록한다.
     *
     * @param event 수신된 도메인 이벤트 객체
     * @param xDeath AMQP 메시지 헤더에서 추출한 메시지 거절 정보
     */
    protected void handleEvent(T event, List<Map<String, Object>> xDeath) {
        if (event instanceof ExpirableEvent expirableEvent && expirableEvent.isExpired()) {
            log.info("[Event Drop] Event {} is expired.", event.getEventId());
            return;
        }
        
        if (processedEventStore.isProcessed(event)) {
            log.info("[Event Drop] Event {} is already processed.", event.getEventId());
            return;
        }
        
        try {
            process(event);
            Duration duration = resolveProcessedEventDuration(event);
            
            if (duration.isNegative() || duration.isZero()) {
                return;
            }
            
            processedEventStore.markProcessed(event, duration);
        } catch (RuntimeException e) {
            if (e instanceof RetryableException retryable && retryable.isRetryable()) {
                int deathCount = getDeathCount(xDeath);
                int nextDeathCount = deathCount + 1;
                
                if (nextDeathCount >= getMaxRetryCount()) {
                    dead(event, e);
                    return;
                }
                
                retry(event, e, nextDeathCount);
            } else {
                dead(event, e);
            }
        }
    }
    
    /**
     * 처리 성공 이벤트의 성공 여부 기록 보관 기간을 구하는 메서드
     *
     * <p> 해당 이벤트가 {@link ExpirableEvent}일 경우, 현재 시간을 기준으로 만료 기한을 계산해서 반환한다.
     * <p> 그렇지 않을 경우, {@link #DEFAULT_NON_EXPIRABLE_EVENT_DURATION}을 반환한다.
     *
     * @param event 도메인 이벤트
     * @return      이벤트 성공 여부 기록 보관 기간
     */
    private Duration resolveProcessedEventDuration(T event) {
        if (event instanceof ExpirableEvent expirableEvent) {
            return Duration.between(
                    ZonedDateTime.now(ZoneId.of(ZONE)),
                    expirableEvent.getExpiredAt()
            );
        }
        
        return DEFAULT_NON_EXPIRABLE_EVENT_DURATION;
    }
    
    /**
     * AMQP 메시지의 'x-death' 헤더 내 dead count 값을 구하는 메서드
     *
     * <p> 최초 실패 메시지의 경우, 0 반환
     *
     * <p> 'x-death' 헤더 내 'queue', 'reason' 필드를 통한 dead count 필터링
     *
     * <p> 'reject' 상태의 dead count를 구해서 반환
     *
     * @param xDeath AMQP 메시지 내 'x-death' 헤더
     * @return dead count
     */
    protected int getDeathCount(List<Map<String, Object>> xDeath) {
        if (xDeath == null || xDeath.isEmpty()) {
            return 0;
        }
        
        return xDeath.stream()
                .filter(death ->
                        DEATH_REASON_VALUE.equals(death.get(DEATH_REASON_KEY))
                        && getWorkQueueName().equals(death.get(DEATH_QUEUE_KEY))
                )
                .findFirst()
                .map(death -> {
                    Object count = death.get(DEATH_COUNT_KEY);
                    return (count instanceof Number n) ? n.intValue() : 0;
                })
                .orElse(0);
    }
}
