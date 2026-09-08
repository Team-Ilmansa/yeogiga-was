package kr.co.yeogiga.domain.event;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface ExpirableEvent {
    ZoneId ZONE = ZoneId.of("Asia/Seoul");
    
    ZonedDateTime getExpiredAt();
    
    default boolean isExpired() {
        return getExpiredAt().isBefore(ZonedDateTime.now(ZONE));
    }
}
