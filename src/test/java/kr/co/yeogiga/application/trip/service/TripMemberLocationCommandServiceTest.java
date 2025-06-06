package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.route.service.TripLeaderCommandService;
import kr.co.yeogiga.application.trip.dto.TripMemberLocationDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripMemberLocationCommandServiceTest {

    @Mock
    private TripMemberService tripMemberService;

    @Mock
    private TripService tripService;

    @Mock
    private TripLeaderCommandService tripLeaderCommandService;

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private TripMemberLocationCommandService tripMemberLocationCommandService;

    @Nested
    @DisplayName("사용자 위치 저장")
    class SaveLocation {
        private final Long tripId = 1L;
        private final Long userId = 1L;
        private final Trip trip = Trip.builder()
                .leaderId(userId)
                .build();

        @BeforeEach
        void setUp() {
            LocalDateTime start = LocalDateTime.of(2025, 6, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2025, 6, 5, 0, 0);
            trip.updateTime(start, end);
        }

        @Test
        @DisplayName("성공 - 방장")
        void successIsLeader() {
            // given
            TripMemberLocationDto.Request request
                    = new TripMemberLocationDto.Request(1.1, 2.2);

            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            doNothing().when(redisRepository).setHash(anyString(), anyString(), any());
            doNothing().when(redisRepository).setHashExpire(anyString(), anyString(), any());
            when(tripService.readById(tripId)).thenReturn(Optional.ofNullable(trip));

            // when
            tripMemberLocationCommandService.saveLocation(tripId, userId, request);

            // then
            verify(redisRepository, times(1)).setHash(isA(String.class), isA(String.class), isA(TripMemberLocationDto.StoredFormat.class));
            verify(redisRepository, times(1)).setHashExpire(isA(String.class), isA(String.class), isA(Duration.class));
            verify(tripLeaderCommandService, times(1)).storeLeaderRouteInRedis(isA(Long.class), isA(Integer.class), isA(Double.class), isA(Double.class));
        }

        @Test
        @DisplayName("성공 - 방장 아님")
        void successIsNotLeader() {
            // given
            TripMemberLocationDto.Request request
                    = new TripMemberLocationDto.Request(1.1, 2.2);
            Long memberId = 2L;

            when(tripMemberService.existsByTripIdAndUserId(tripId, memberId)).thenReturn(true);
            doNothing().when(redisRepository).setHash(anyString(), anyString(), any());
            doNothing().when(redisRepository).setHashExpire(anyString(), anyString(), any());
            when(tripService.readById(tripId)).thenReturn(Optional.ofNullable(trip));

            // when
            tripMemberLocationCommandService.saveLocation(tripId, memberId, request);

            // then
            verify(redisRepository, times(1)).setHash(isA(String.class), isA(String.class), isA(TripMemberLocationDto.StoredFormat.class));
            verify(redisRepository, times(1)).setHashExpire(isA(String.class), isA(String.class), isA(Duration.class));
            verify(tripLeaderCommandService, never()).storeLeaderRouteInRedis(isA(Long.class), isA(Integer.class), isA(Double.class), isA(Double.class));
        }

        @Test
        @DisplayName("실패 - 여행 미존재 또는 멤버가 아닌 경우")
        void failIfTripNotFoundOrIsNotMember() {
            // given
            TripMemberLocationDto.Request request
                    = new TripMemberLocationDto.Request(1.1, 2.2);

            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(false);

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripMemberLocationCommandService.saveLocation(tripId, userId, request));

            // then
            assertEquals(TripMemberErrorType.IS_NOT_MEMBER, exception.getErrorType());
        }
    }
}
