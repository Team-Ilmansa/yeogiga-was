package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripPlaceSavingServiceTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripPlaceSavingService tripPlaceSavingService;

    private final Long tripId = 1L;
    private final int lastDay = 2;

    private Trip trip = Trip.builder()
            .title("title")
            .travelStatus(TravelStatus.SETTING)
            .leaderId(1L)
            .build();

    @Test
    @DisplayName("여행 생성 완료 성공 - 여행 전")
    void completeTripSuccessPlanned() {
        // given
        TripPlaceReq.StoredFormat place1 = new TripPlaceReq.StoredFormat(
                "id1", "장소1", 33.123, 126.456, "관광지"
        );
        TripPlaceReq.StoredFormat place2 = new TripPlaceReq.StoredFormat(
                "id2", "장소2", 33.789, 126.987, "식당"
        );

        when(redisRepository.getList(anyString(), eq(TripPlaceReq.StoredFormat.class)))
                .thenReturn(List.of(place1))
                .thenReturn(List.of(place2));


        ReflectionTestUtils.setField(trip, "startedAt", LocalDateTime.of(2025, 5, 25, 12, 0));
        ReflectionTestUtils.setField(trip, "endedAt", LocalDateTime.of(2025, 5, 26, 12, 0));

        when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String date = "2025-05-24T12:00:00Z";
            Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
            LocalDateTime mockNow = LocalDateTime.now(clock);
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

            // when
            tripPlaceSavingService.completeTrip(tripId, lastDay);

            // then
            verify(redisRepository, times(2)).getList(anyString(), eq(TripPlaceReq.StoredFormat.class));
            verify(tripDayPlaceService, times(1)).saveAll(any());
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 2));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 2));

            assertEquals(TravelStatus.PLANNED, trip.getTravelStatus());
        }
    }

    @Test
    @DisplayName("여행 생성 완료 성공 - 여행 중")
    void completeTripSuccessInProgress() {
        // given
        TripPlaceReq.StoredFormat place1 = new TripPlaceReq.StoredFormat(
                "id1", "장소1", 33.123, 126.456, "관광지"
        );
        TripPlaceReq.StoredFormat place2 = new TripPlaceReq.StoredFormat(
                "id2", "장소2", 33.789, 126.987, "식당"
        );

        when(redisRepository.getList(anyString(), eq(TripPlaceReq.StoredFormat.class)))
                .thenReturn(List.of(place1))
                .thenReturn(List.of(place2));


        ReflectionTestUtils.setField(trip, "startedAt", LocalDateTime.of(2025, 5, 25, 12, 0));
        ReflectionTestUtils.setField(trip, "endedAt", LocalDateTime.of(2025, 5, 26, 12, 0));

        when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String date = "2025-05-25T18:00:00Z";
            Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("Asia/Seoul"));
            LocalDateTime mockNow = LocalDateTime.now(clock);
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

            // when
            tripPlaceSavingService.completeTrip(tripId, lastDay);

            // then
            verify(redisRepository, times(2)).getList(anyString(), eq(TripPlaceReq.StoredFormat.class));
            verify(tripDayPlaceService, times(1)).saveAll(any());
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 2));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 2));

            assertEquals(TravelStatus.IN_PROGRESS, trip.getTravelStatus());
        }
    }

    @Test
    @DisplayName("여행 생성 완료 성공 - 여행 후")
    void completeTripSuccessCompleted() {
        // given
        TripPlaceReq.StoredFormat place1 = new TripPlaceReq.StoredFormat(
                "id1", "장소1", 33.123, 126.456, "관광지"
        );
        TripPlaceReq.StoredFormat place2 = new TripPlaceReq.StoredFormat(
                "id2", "장소2", 33.789, 126.987, "식당"
        );

        when(redisRepository.getList(anyString(), eq(TripPlaceReq.StoredFormat.class)))
                .thenReturn(List.of(place1))
                .thenReturn(List.of(place2));


        ReflectionTestUtils.setField(trip, "startedAt", LocalDateTime.of(2025, 5, 20, 12, 0));
        ReflectionTestUtils.setField(trip, "endedAt", LocalDateTime.of(2025, 5, 21, 12, 0));

        when(tripService.readById(tripId)).thenReturn(Optional.of(trip));

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String date = "2025-05-24T12:00:00Z";
            Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
            LocalDateTime mockNow = LocalDateTime.now(clock);
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);

            // when
            tripPlaceSavingService.completeTrip(tripId, lastDay);

            // then
            verify(redisRepository, times(2)).getList(anyString(), eq(TripPlaceReq.StoredFormat.class));
            verify(tripDayPlaceService, times(1)).saveAll(any());
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 2));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 2));

            assertEquals(TravelStatus.COMPLETED, trip.getTravelStatus());
        }
    }
}
