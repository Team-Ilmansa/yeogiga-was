package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripQueryServiceTest {

    @Mock
    private TripService tripService;

    @Mock
    private TripMemberService tripMemberService;

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @InjectMocks
    private TripQueryService tripQueryService;

    @Nested
    @DisplayName("메인 화면 내 여행 조회")
    class TripInfoInMain {
        private final Long userId = 1L;

        private Trip trip1 = Trip.builder()
                .title("title")
                .leaderId(userId)
                .travelStatus(TravelStatus.PLANNED)
                .build();
  
        private Trip trip2 = Trip.builder()
                .title("title")
                .leaderId(userId)
                .travelStatus(TravelStatus.PLANNED)
                .build();

        @Test
        @DisplayName("진행 전인 여행만 존재하는 경우")
        void successIfTripOnlyPlanned() {
            // given
            ReflectionTestUtils.setField(trip1, "id", 1L);
            ReflectionTestUtils.setField(trip1, "startedAt", LocalDateTime.of(2025, 5, 20, 12, 0));
            ReflectionTestUtils.setField(trip1, "endedAt", LocalDateTime.of(2025, 5, 21, 12, 0));

            ReflectionTestUtils.setField(trip2, "id", 2L);
            ReflectionTestUtils.setField(trip2, "startedAt", LocalDateTime.of(2025, 5, 22, 12, 0));
            ReflectionTestUtils.setField(trip2, "endedAt", LocalDateTime.of(2025, 5, 23, 12, 0));

            Place place = Place.builder()
                    .id("place-id")
                    .name("두류 공원")
                    .latitude(11.11)
                    .longitude(12.12)
                    .order(10.0)
                    .placeType("관광 명소")
                    .build();

            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of(trip1, trip2));
            when(tripDayPlaceService.readTripDayPlaceByTripIdAndDay(eq(trip1.getId()), eq(1))).thenReturn(List.of(place));

            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-05-15T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                TripRes.TripMainInfo result = tripQueryService.getTripMainInfo(userId);

                // then
                assertEquals(trip1.getId(), result.tripId());
                assertEquals(trip1.getTitle(), result.title());
                assertEquals(1, result.day());
                assertThat(result.places()).hasSize(1);
            }
        }

        @Test
        @DisplayName("진행 전인 여행에 목적지가 아직 없는 경우")
        void successIfTripPlannedDoseNotHavePlace() {
            // given
            ReflectionTestUtils.setField(trip1, "id", 1L);
            ReflectionTestUtils.setField(trip1, "startedAt", LocalDateTime.of(2025, 5, 20, 12, 0));
            ReflectionTestUtils.setField(trip1, "endedAt", LocalDateTime.of(2025, 5, 21, 12, 0));

            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of(trip1, trip2));
            when(tripDayPlaceService.readTripDayPlaceByTripIdAndDay(eq(trip1.getId()), eq(1))).thenReturn(List.of());

            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-05-15T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                TripRes.TripMainInfo result = tripQueryService.getTripMainInfo(userId);

                // then
                assertEquals(trip1.getId(), result.tripId());
                assertEquals(trip1.getTitle(), result.title());
                assertEquals(1, result.day());
                assertThat(result.places()).hasSize(0);
            }
        }

        @Test
        @DisplayName("진행 중인 여행이 존재하는 경우")
        void successIfTripPlannedAndInProgress() {
            // given
            ReflectionTestUtils.setField(trip1, "id", 1L);
            ReflectionTestUtils.setField(trip1, "travelStatus", TravelStatus.IN_PROGRESS);
            ReflectionTestUtils.setField(trip1, "startedAt", LocalDateTime.of(2025, 5, 10, 12, 0));
            ReflectionTestUtils.setField(trip1, "endedAt", LocalDateTime.of(2025, 5, 20, 12, 0));

            ReflectionTestUtils.setField(trip2, "id", 2L);
            ReflectionTestUtils.setField(trip2, "startedAt", LocalDateTime.of(2025, 5, 20, 12, 0));
            ReflectionTestUtils.setField(trip2, "endedAt", LocalDateTime.of(2025, 5, 21, 12, 0));

            Place place = Place.builder()
                    .id("place-id")
                    .name("두류 공원")
                    .latitude(11.11)
                    .longitude(12.12)
                    .order(10.0)
                    .placeType("관광 명소")
                    .build();

            TripDayPlace tripDayPlace = TripDayPlace.builder()
                    .tripId(trip1.getId())
                    .day(1)
                    .places(List.of(place))
                    .build();

            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of(trip1, trip2));
            when(tripDayPlaceService.readTripDayPlaceByTripIdAndDay(eq(trip1.getId()), eq(6))).thenReturn(List.of(place));

            try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
                String date = "2025-05-15T12:00:00Z";
                Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
                LocalDateTime mockNow = LocalDateTime.now(clock);
                mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

                // when
                TripRes.TripMainInfo result = tripQueryService.getTripMainInfo(userId);

                // then
                assertEquals(trip1.getId(), result.tripId());
                assertEquals(trip1.getTitle(), result.title());
                assertEquals(6, result.day());
                assertThat(result.places()).hasSize(1);
            }
        }

        @Test
        @DisplayName("진행 중 또는 진행 전인 여행이 없는 경우")
        void successIfNoTripInProgress() {
            // given
            ReflectionTestUtils.setField(trip1, "id", 1L);
            ReflectionTestUtils.setField(trip1, "travelStatus", TravelStatus.COMPLETED);
            ReflectionTestUtils.setField(trip1, "startedAt", LocalDateTime.of(2025, 5, 10, 12, 0));
            ReflectionTestUtils.setField(trip1, "endedAt", LocalDateTime.of(2025, 5, 11, 12, 0));

            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of(trip1));

            // when
            TripRes.TripMainInfo result = tripQueryService.getTripMainInfo(userId);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("전체 여행 목록 조회")
    class GetAllTrip {
        private final Long userId = 1L;

        private Trip trip = Trip.builder()
                .title("title")
                .city("대구광역시")
                .leaderId(userId)
                .travelStatus(TravelStatus.PLANNED)
                .build();
      
        private User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("test@test.com")
                .role(Role.USER)
                .build();


        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of(trip));
            when(tripMemberService.readAllUserByTripId(any())).thenReturn(List.of(user));

            // when
            List<TripRes.TripSummary> result = tripQueryService.getAllTrip(userId);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 빈 배열 반환")
        void successEmptyList() {
            // given
            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of());

            // when
            List<TripRes.TripSummary> result = tripQueryService.getAllTrip(userId);

            // then
            assertThat(result).hasSize(0);
        }
    }

    @Nested
    @DisplayName("특정 여행 조회")
    class GetTrip {

        private User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("test@test.com")
                .role(Role.USER)
                .build();

        private Trip trip = Trip.builder()
                .title("title")
                .city("대구광역시")
                .leaderId(1L)
                .travelStatus(TravelStatus.IN_PROGRESS)
                .build();

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(trip, "id", 1L);
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripService.readById(trip.getId())).thenReturn(Optional.of(trip));
            when(tripMemberService.readAllUserByTripId(trip.getId())).thenReturn((List.of(user)));

            // when
            TripRes.TripSummary result = tripQueryService.getTrip(1L);

            // then
            assertEquals(trip.getId(), result.tripId());
            assertEquals(trip.getTitle(), result.title());
            assertThat(result.members()).hasSize(1);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 여행")
        void failIfTripNotFound() {
            // given
            when(tripService.readById(trip.getId())).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripQueryService.getTrip(1L));

            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("준비 중 여행 조회")
    class GetSettingTrip {
        private final Long userId = 1L;

        private Trip trip = Trip.builder()
                .title("title")
                .leaderId(userId)
                .travelStatus(TravelStatus.SETTING)
                .build();

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(trip, "id", 1L);
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripMemberService.readAllSettingTripByUserId(userId)).thenReturn(List.of(trip));

            // when
            List<TripRes.SettingTripInfo> result = tripQueryService.getSettingTrip(userId);

            // then
            System.out.println(result);
            assertThat(result).hasSize(1);

            TripRes.SettingTripInfo settingTripInfo = result.get(0);
            assertEquals(trip.getId(), settingTripInfo.tripId());
            assertEquals(trip.getTitle(), settingTripInfo.title());
            assertEquals(TravelStatus.SETTING, settingTripInfo.status());
        }
    }
}
