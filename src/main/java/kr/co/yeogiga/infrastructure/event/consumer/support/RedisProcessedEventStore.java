package kr.co.yeogiga.infrastructure.event.consumer.support;

import kr.co.yeogiga.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisProcessedEventStore implements ProcessedEventStore {
    private final StringRedisTemplate stringRedisTemplate;
    
    private static final String PROCESSED_EVENT_KEY_PREFIX = "event:processed:";
    
    /**
     * 해당 이벤트의 처리 상태 여부를 반환하는 메서드
     *
     * <p> Redis에 {@value PROCESSED_EVENT_KEY_PREFIX}로 시작하는 이벤트 아이디 키가 존재하는지 여부를 확인한다.
     *
     * @param event 도메인 이벤트
     * @return      해당 이벤트가 처리 되었으면 true, 아니면 false
     */
    @Override
    public boolean isProcessed(DomainEvent event) {
        return stringRedisTemplate.hasKey(PROCESSED_EVENT_KEY_PREFIX + event.getEventId());
    }
    
    /**
     * 해당 이벤트의 처리 완료 여부와 기록 기한를 기록하는 메서드
     *
     * <p> {@value PROCESSED_EVENT_KEY_PREFIX}와 도메인 이벤트 아이디({@code eventId})를 조합한 키를 생성한다.
     * <p> 해당 키를 {@code duration}만큼 TTL을 설정하여 저장한다.
     *
     * @param event     도메인 이벤트
     * @param duration  기록 기한
     */
    @Override
    public void markProcessed(DomainEvent event, Duration duration) {
        try {
            stringRedisTemplate.opsForValue().set(PROCESSED_EVENT_KEY_PREFIX + event.getEventId(), "1", duration);
        } catch (Exception e) {
             log.warn("[PROCESSED-EVENT] Failed to mark event \"{}\".", event.getEventId(), e);
        }
    }
}
