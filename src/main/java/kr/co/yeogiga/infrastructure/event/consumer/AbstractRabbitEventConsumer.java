package kr.co.yeogiga.infrastructure.event.consumer;

import kr.co.yeogiga.domain.event.DomainEvent;
import kr.co.yeogiga.infrastructure.event.exception.RetryableException;

import java.util.List;
import java.util.Map;

public abstract class AbstractRabbitEventConsumer<T extends DomainEvent> {
    private final String DEATH_REASON_KEY = "reason";
    private final String DEATH_QUEUE_KEY = "queue";
    private final String DEATH_COUNT_KEY = "count";
    private final String DEATH_REASON_VALUE = "rejected";
    
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
     * <p> 비즈니스 로직 실행 중 예외 발생 시, 해당 예외에 대한 재시도 가능 여부를 확인
     *
     * <p> 재시도가 가능한 경우, 'x-death' 헤더를 분석하여 현재 재시도 횟수가 최대 허용치({@code getMaxRetryCount()}를 초과했는지 확인
     *
     * <p> 최대 재시도 횟수를 초과했거나 재시도가 불가능한 예외가 발생한 경우, 해당 메시지를 Dead Letter 처리
     *
     * @param event 수신된 도메인 이벤트 객체
     * @param xDeath AMQP 메시지 헤더에서 추출한 메시지 거절 정보
     */
    protected void handleEvent(T event, List<Map<String, Object>> xDeath) {
        try {
            process(event);
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
