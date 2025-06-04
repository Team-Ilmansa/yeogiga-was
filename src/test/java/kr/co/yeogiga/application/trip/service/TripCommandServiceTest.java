package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.fcm.service.TripPushSender;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.dto.TripFcmTokenQueryDto;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.TripMemberTokenConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripCommandServiceTest {
    @Mock
    private TripService tripService;

    @Mock
    private TripMemberService tripMemberService;

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @Mock
    private UserService userService;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private TripPushSender tripPushSender;

    @InjectMocks
    private TripCommandService tripCommandService;

    @Nested
    @DisplayName("여행 생성")
    class TripCreation {
        private final Long leaderId = 1L;

        private TripReq.Creation creationRequest = TripReq.Creation.builder()
                .title("test")
                .build();

        @Captor
        private ArgumentCaptor<Trip> tripCaptor;

        @Test
        @DisplayName("성공")
        void success() {
            // given
            User user = User.builder()
                    .username("username")
                    .password("password")
                    .nickname("nickname")
                    .email("email")
                    .role(Role.USER)
                    .build();

            Trip trip = Trip.builder()
                    .title(creationRequest.title())
                    .travelStatus(TravelStatus.PLANNED)
                    .leaderId(leaderId)
                    .build();

            ReflectionTestUtils.setField(trip, "id", 1L);

            when(userService.readById(any())).thenReturn(Optional.of(user));
            when(tripService.save(any())).thenReturn(trip);
            doNothing().when(tripMemberService).save(any());


            // when
            Long newTripId = tripCommandService.create(leaderId, creationRequest);

            // then
            verify(tripService).save(tripCaptor.capture());

            Trip capturedTrip = tripCaptor.getValue();
            assertEquals(creationRequest.title(), capturedTrip.getTitle());
            assertEquals(leaderId, capturedTrip.getLeaderId());
            assertEquals(TravelStatus.SETTING, capturedTrip.getTravelStatus());

            assertEquals(1L, newTripId);
        }
    }

    @Nested
    @DisplayName("시간 수정")
    class TimeModification {
        private final Long tripId = 1L;
        private final Long userId = 1L;

        private Trip trip = Trip.builder()
                .title("test")
                .city("대구광역시")
                .leaderId(userId)
                .travelStatus(TravelStatus.PLANNED)
                .build();

        @Test
        @DisplayName("성공 - 여행 전")
        void successBeforeTrip() {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 5, 2, 12, 00);
            TripReq.Time time = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

            // LocalDateTime::now static 메서드에 대한 반환값 Stub
            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-03-01T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                tripCommandService.updateTime(tripId, userId, time);

                // then
                assertEquals(TravelStatus.PLANNED, trip.getTravelStatus());
            }
        }

        @Test
        @DisplayName("성공 - 여행 중")
        void successOnTrip() {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 2, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 4, 2, 12, 00);
            TripReq.Time time = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

            // LocalDateTime::now static 메서드에 대한 반환값 Stub
            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-03-01T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                tripCommandService.updateTime(tripId, userId, time);

                // then
                assertEquals(TravelStatus.IN_PROGRESS, trip.getTravelStatus());
            }
        }

        @Test
        @DisplayName("성공 - 여행 후")
        void successAfterTrip() {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 2, 2, 12, 00);
            TripReq.Time time = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

            // LocalDateTime::now static 메서드에 대한 반환값 Stub
            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-03-01T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                tripCommandService.updateTime(tripId, userId, time);

                // then
                assertEquals(TravelStatus.COMPLETED, trip.getTravelStatus());
            }
        }

        @Test
        @DisplayName("여행 기간이 늘어난 경우 - 부족한 일차 데이터를 생성")
        void tripDayPlaceIncreaseTest() {
            // given
            LocalDateTime currentStart = LocalDateTime.of(2025, 6, 1, 0, 0);
            LocalDateTime currentEnd = LocalDateTime.of(2025, 6, 3, 0, 0);
            LocalDateTime newStart = LocalDateTime.of(2025, 6, 1, 0, 0);
            LocalDateTime newEnd = LocalDateTime.of(2025, 6, 5, 0, 0);

            Trip trip = Trip.builder()
                    .leaderId(userId)
                    .travelStatus(TravelStatus.SETTING)
                    .build();
            trip.updateTime(currentStart, currentEnd);

            TripReq.Time time = new TripReq.Time(newStart, newEnd);

            given(tripService.readById(tripId)).willReturn(Optional.of(trip));

            // when
            tripCommandService.updateTime(tripId, userId, time);

            // then
            verify(tripDayPlaceService, times(2)).save(any());
            verify(tripDayPlaceService, never()).deleteByTripIdAndDayGreaterThan(anyLong(), anyInt());
        }

        @Test
        @DisplayName("여행 기간이 줄어든 경우 - 초과 일차 데이터를 삭제")
        void tripDayPlaceDecreaseTest() {
            // given
            LocalDateTime currentStart = LocalDateTime.of(2025, 6, 1, 0, 0);
            LocalDateTime currentEnd = LocalDateTime.of(2025, 6, 5, 0, 0);
            LocalDateTime newStart = LocalDateTime.of(2025, 6, 1, 0, 0);
            LocalDateTime newEnd = LocalDateTime.of(2025, 6, 3, 0, 0);

            Trip trip = Trip.builder()
                    .leaderId(userId)
                    .travelStatus(TravelStatus.SETTING)
                    .build();
            trip.updateTime(currentStart, currentEnd);

            TripReq.Time time = new TripReq.Time(newStart, newEnd);

            given(tripService.readById(tripId)).willReturn(Optional.of(trip));

            // when
            tripCommandService.updateTime(tripId, userId, time);

            // then
            verify(tripDayPlaceService, never()).save(any());
            verify(tripDayPlaceService, times(1)).deleteByTripIdAndDayGreaterThan(anyLong(), anyInt());
        }

        @Test
        @DisplayName("실패 - 여행 시각 오류 (종료 시각 <= 출발 시각")
        void failInvalidDateRange() {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 3, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 2, 2, 12, 00);
            TripReq.Time time = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

            // LocalDateTime::now static 메서드에 대한 반환값 Stub
            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-03-01T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                CustomException exception = assertThrows(CustomException.class, () -> tripCommandService.updateTime(tripId, userId, time));

                // then
                assertEquals(TripErrorType.INVALID_DATE_RANGE, exception.getErrorType());
            }
        }

        @Test
        @DisplayName("실패 - 방장 아닌 사용자 요청")
        void failForbidden() {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 2, 2, 12, 00);
            TripReq.Time time = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

            // LocalDateTime::now static 메서드에 대한 반환값 Stub
            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-03-01T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                CustomException exception = assertThrows(CustomException.class, () -> tripCommandService.updateTime(tripId, 2L, time));

                // then
                assertEquals(TripErrorType.PERMISSION_DENIED_NOT_LEADER, exception.getErrorType());
            }
        }
    }

    @Nested
    @DisplayName("여행 상태 변경")
    class UpdateTravelStatus {

        private final Long tripId = 1L;

        @Test
        @DisplayName("시작되는 여행이 존재하는 경우 - Redis에 멤버들의 Fcm Token을 저장")
        void shouldStoreFcmTokensToRedisWhenTripStarts() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 5, 29, 12, 0);
            TripFcmTokenQueryDto dto = new TripFcmTokenQueryDto(1L, "여행 제목", "fcm-token", now.plusDays(1));
            given(tripService.readTripFcmTokensByTime(now)).willReturn(List.of(dto));

            // when
            tripCommandService.updateTravelStatus(now);

            // then
            String redisKey = TripMemberTokenConstant.tripTokenKey(tripId);
            verify(redisRepository, times(1)).setListAll(redisKey, List.of("fcm-token"));
            verify(redisRepository, times(1)).expire(anyString(), any());
            verify(tripService, times(1)).updateAllTravelStatusToInProgress(now);
            verify(tripService, times(1)).updateAllTravelStatusToCompleted(now);
            verify(tripPushSender, times(1)).sendPush(anyLong(), anyString(), anyString(), anyList());
        }

        @Test
        @DisplayName("시작되는 여행이 없는 경우 - 아무것도 저장하지 않고 상태만 갱신")
        void shouldOnlyUpdateStatusWhenNoTripsToStart() {
            // given
            LocalDateTime now = LocalDateTime.of(2025, 5, 29, 14, 0);
            given(tripService.readTripFcmTokensByTime(now)).willReturn(List.of());

            // when
            tripCommandService.updateTravelStatus(now);

            // then
            verify(redisRepository, never()).setListAll(any(), any());
            verify(redisRepository, never()).expire(any(), any());
            verify(tripService, times(1)).updateAllTravelStatusToCompleted(now);
        }
    }

    @Nested
    @DisplayName("여행 삭제")
    class TripDeletion {
        private final Long tripId = 1L;

        @Test
        @DisplayName("성공")
        void success() {
            // given
            doNothing().when(tripService).deleteById(tripId);

            // when
            tripCommandService.removeTrip(tripId);

            // then
            verify(tripService, times(1)).deleteById(tripId);
        }
    }

    @Nested
    @DisplayName("여행 정보 수정")
    class TripInfoModification {
        private final Long tripId = 1L;
        private final Long userId = 1L;

        private Trip trip = Trip.builder()
                .title("title")
                .leaderId(userId)
                .travelStatus(TravelStatus.COMPLETED)
                .build();

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(trip, "id", tripId);
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            TripReq.Update updateRequest = TripReq.Update.builder()
                    .title("new title")
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

            // when
            tripCommandService.updateTripInfo(tripId, updateRequest);

            // then
            assertEquals(updateRequest.title(), trip.getTitle());
        }

        @Test
        @DisplayName("실패 - 여행 미존재")
        void failIfTripNotFound() {
            // given
            TripReq.Update updateRequest = TripReq.Update.builder()
                    .title("new title")
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripCommandService.updateTripInfo(tripId, updateRequest));

            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 기존과 동일한 여행 제목")
        void failIfSameTitle() {
            // given
            TripReq.Update updateRequest = TripReq.Update.builder()
                    .title("title")
                    .build();

            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripCommandService.updateTripInfo(tripId, updateRequest));

            // then
            assertEquals(TripErrorType.SAME_TRIP_TITLE, exception.getErrorType());
        }
    }
}

