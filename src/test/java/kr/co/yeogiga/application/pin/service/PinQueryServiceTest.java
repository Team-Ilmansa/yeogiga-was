package kr.co.yeogiga.application.pin.service;

import kr.co.yeogiga.domain.pin.entity.Pin;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PinConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PinQueryServiceTest {

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private PinQueryService pinQueryService;

    @Nested
    @DisplayName("핀 조회")
    class GetPin {
        private final Long tripId = 1L;

        private Pin pin = Pin.builder()
                .place("경상북도 경산시 대학로 280")
                .latitude(1.1)
                .longitude(2.2)
                .time(LocalDateTime.of(2025, 5, 26, 13, 0))
                .build();

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(redisRepository.get(PinConstant.pinKey(tripId))).thenReturn(pin);

            // when
            Pin result = pinQueryService.getPin(tripId);

            // then
            assertEquals(pin.getPlace(), result.getPlace());
            assertEquals(pin.getLatitude(), result.getLatitude());
            assertEquals(pin.getLongitude(), result.getLongitude());
            assertEquals(pin.getTime(), result.getTime());
        }
    }
}
