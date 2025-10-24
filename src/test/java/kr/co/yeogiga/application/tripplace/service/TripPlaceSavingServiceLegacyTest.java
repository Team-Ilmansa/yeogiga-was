package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReqLegacy;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripPlaceSavingServiceLegacyTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripPlaceSavingServiceLegacy tripPlaceSavingServiceLegacy;

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
        TripPlaceReqLegacy.StoredFormat place1 = new TripPlaceReqLegacy.StoredFormat(
                "id1", "장소1", "주소1", 33.123, 126.456, PlaceCategory.TOURISM
        );
        TripPlaceReqLegacy.StoredFormat place2 = new TripPlaceReqLegacy.StoredFormat(
                "id2", "장소2", "주소2", 33.789, 126.987, PlaceCategory.RESTAURANT
        );

        when(redisRepository.getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class)))
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
            tripPlaceSavingServiceLegacy.completeTrip(tripId, lastDay);

            // then
            verify(redisRepository, times(4)).getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class));
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
        TripPlaceReqLegacy.StoredFormat place1 = new TripPlaceReqLegacy.StoredFormat(
                "id1", "장소1", "주소1", 33.123, 126.456, PlaceCategory.TOURISM
        );
        TripPlaceReqLegacy.StoredFormat place2 = new TripPlaceReqLegacy.StoredFormat(
                "id2", "장소2", "주소2", 33.789, 126.987, PlaceCategory.RESTAURANT
        );

        when(redisRepository.getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class)))
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
            tripPlaceSavingServiceLegacy.completeTrip(tripId, lastDay);

            // then
            verify(redisRepository, times(4)).getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class));
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
        TripPlaceReqLegacy.StoredFormat place1 = new TripPlaceReqLegacy.StoredFormat(
                "id1", "장소1", "주소1", 33.123, 126.456, PlaceCategory.TOURISM
        );
        TripPlaceReqLegacy.StoredFormat place2 = new TripPlaceReqLegacy.StoredFormat(
                "id2", "장소2", "주소2", 33.789, 126.987, PlaceCategory.RESTAURANT
        );

        when(redisRepository.getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class)))
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
            tripPlaceSavingServiceLegacy.completeTrip(tripId, lastDay);

            // then
            verify(redisRepository, times(4)).getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class));
            verify(tripDayPlaceService, times(1)).saveAll(any());
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 1));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlacesKey(tripId, 2));
            verify(redisRepository, times(1)).del(PlaceConstant.dayPlaceSetKey(tripId, 2));

            assertEquals(TravelStatus.COMPLETED, trip.getTravelStatus());
        }
    }
    
    @Test
    @DisplayName("여행 목적지 확정 성공 - 여행 주 목적지(지역) 할당 테스트")
    void successAssignTripCity() {
        // given
        TripPlaceReqLegacy.StoredFormat place1 = new TripPlaceReqLegacy.StoredFormat(
                "id1", "달성공원", "대구광역시 중구 달성공원로35", 33.123, 126.456, PlaceCategory.TOURISM
        );
        TripPlaceReqLegacy.StoredFormat place2 = new TripPlaceReqLegacy.StoredFormat(
                "id2", "포함시청", "경상북도 포항시 남구 대잠동 1001 포항시청", 33.789, 126.987, PlaceCategory.TOURISM
        );
        TripPlaceReqLegacy.StoredFormat place3 = new TripPlaceReqLegacy.StoredFormat(
                "id3", "포항역 고속철", "경상북도 포항시 북구 흥해읍 이인리 137-1", 33.789, 126.987, PlaceCategory.TRANSPORT
        );
        TripPlaceReqLegacy.StoredFormat place4 = new TripPlaceReqLegacy.StoredFormat(
                "id4", "한라산", "제주특별자치도 서귀포시 토평동 산15-1", 33.789, 126.987, PlaceCategory.TOURISM
        );
        
        when(redisRepository.getList(PlaceConstant.dayPlacesKey(tripId, 1), TripPlaceReqLegacy.StoredFormat.class))
                .thenReturn(List.of(place1, place2));
                
        
        when(redisRepository.getList(PlaceConstant.dayPlacesKey(tripId, 2), TripPlaceReqLegacy.StoredFormat.class))
                .thenReturn(List.of(place3, place4));
        
        ReflectionTestUtils.setField(trip, "startedAt", LocalDateTime.of(2025, 5, 20, 12, 0));
        ReflectionTestUtils.setField(trip, "endedAt", LocalDateTime.of(2025, 5, 21, 12, 0));
        
        when(tripService.readById(tripId)).thenReturn(Optional.of(trip));
        
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            String date = "2025-05-24T12:00:00Z";
            Clock clock = Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
            LocalDateTime mockNow = LocalDateTime.now(clock);
            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(mockNow);
            
            // when
            tripPlaceSavingServiceLegacy.completeTrip(tripId, 2);
            
            // then: 여행 목적지(지역)은 중복인 목적지로 인하여 총 3곳이며, "대구광역시", "포항시", "제주특별자치도"이다.
            assertThat(trip.getCity()).hasSize(3);
            assertThat(trip.getCity()).containsAll(List.of("대구광역시", "포항시", "제주특별자치도"));
        }
    }
}
