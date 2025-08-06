package kr.co.yeogiga.application.pin.service;

import kr.co.yeogiga.application.fcm.service.TripPushSender;
import kr.co.yeogiga.application.pin.dto.PinReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.domain.pin.entity.Pin;
import kr.co.yeogiga.domain.trip.dto.TripFcmTokenInfoDto;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PinConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PinCommandServiceTest {

    @Mock
    private TripService tripService;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private TripPushSender tripPushSender;

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
            TripFcmTokenInfoDto fcmTokenInfoDto = new TripFcmTokenInfoDto(1L, "title", "fcm-token");

            when(tripService.existsById(tripId)).thenReturn(true);
            when(tripService.readTripFcmTokenInfosById(tripId)).thenReturn(List.of(fcmTokenInfoDto));
            doNothing().when(tripPushSender).sendPush(anyLong(), anyString(), anyString(), anyList());

            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-05-25T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                pinCommandService.createPin(tripId, request);

                // then
                verify(redisRepository, times(1)).set(isA(String.class), isA(Pin.class), isA(Duration.class));
                verify(tripPushSender, times(1)).sendPush(anyLong(), anyString(), anyString(), anyList());
            }

        }

        @Test
        @DisplayName("성공 - FCM 토큰 저장 정보가 없는 경우")
        void successIfFcmTokenNotFound() {
            // given
            when(tripService.existsById(tripId)).thenReturn(true);
            when(tripService.readTripFcmTokenInfosById(tripId)).thenReturn(Collections.emptyList());

            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-05-25T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                pinCommandService.createPin(tripId, request);

                // then
                verify(redisRepository, times(1)).set(isA(String.class), isA(Pin.class), isA(Duration.class));
                verify(tripPushSender,never()).sendPush(anyLong(), anyString(), anyString(), anyList());
            }
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

        @Test
        @DisplayName("실패 - 요청 시각이 현재 시각보다 이전인 경우")
        void failIfRequestTimeIsBeforeNow() {
            when(tripService.existsById(tripId)).thenReturn(true);

            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-07-01T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                CustomException exception = assertThrows(CustomException.class,
                        () -> pinCommandService.createPin(tripId, request));

                // then
                assertEquals(CommonErrorType.TIME_SHOULD_NOT_BEFORE_NOW, exception.getErrorType());
            }
        }
    }

    @Test
    @DisplayName("핀 삭제 성공")
    void deletePinSuccess() {
        // given
        Long tripId = 1L;
        String redisKey = PinConstant.pinKey(tripId);
        doNothing().when(redisRepository).del(redisKey);

        // when
        pinCommandService.deletePin(tripId);

        // then
        verify(redisRepository, times(1)).del(redisKey);
    }

}
