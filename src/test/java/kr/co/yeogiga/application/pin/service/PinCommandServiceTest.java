package kr.co.yeogiga.application.pin.service;

import kr.co.yeogiga.application.pin.dto.PinReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.pin.entity.Pin;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PinCommandServiceTest {

    @Mock
    private TripService tripService;

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private PinCommandService pinCommandService;

    @Nested
    @DisplayName("핀 생성")
    class PinCreation {
        private final Long tripId = 1L;

        private PinReq.Creation request = PinReq.Creation.builder()
                .place("대구광역시 달서구 공원순환로 36")
                .longitude(1.1)
                .latitude(2.2)
                .time(LocalDateTime.of(2025, 5, 26, 12, 0))
                .build();

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripService.existsById(tripId)).thenReturn(true);

            // when
            pinCommandService.createPin(tripId, request);

            // then
            verify(redisRepository, times(1)).set(isA(String.class), isA(Pin.class), isA(Duration.class));
        }

        @Test
        @DisplayName("실패 - 여행 조회 실패")
        void failIfTripNotFound() {
            // given
            when(tripService.existsById(tripId)).thenReturn(false);

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> pinCommandService.createPin(tripId, request));

            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }
    }

}
