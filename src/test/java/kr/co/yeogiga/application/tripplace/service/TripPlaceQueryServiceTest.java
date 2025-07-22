package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripDaySummaryRes;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TripPlaceQueryServiceTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @InjectMocks
    private TripPlaceQueryService tripPlaceQueryService;

    private final Place place1 = Place.builder().id("id1").name("목적지1").latitude(0.0).longitude(1.1).placeType(PlaceCategory.RESTAURANT).order(10.0).build();
    private final Place place2 = Place.builder().id("id2").name("목적지2").latitude(2.2).longitude(3.3).placeType(PlaceCategory.RESTAURANT).order(20.0).build();

    private final Long tripId = 1L;
    private final String tripDayPlaceId = "day1";

    @Test
    @DisplayName("여행 일정 불러오기 테스트 성공")
    void getTripDayPlacesInfoSuccess() {
        // given
        List<Place> places = List.of(place1, place2);

        TripDayPlace tripDayPlace = TripDayPlace.builder()
                .day(1)
                .places(places)
                .build();

        given(tripDayPlaceService.readByTripIdSortedByOrder(tripId)).willReturn(List.of(tripDayPlace));

        // when
        List<TripPlaceRes.TripDayPlaceInfo> result = tripPlaceQueryService.getTripDayPlacesInfo(tripId);

        // then
        assertEquals(2, result.get(0).places().size());
        assertEquals(1, result.get(0).day());
        assertEquals(2, result.get(0).places().size());
    }

    @Nested
    @DisplayName("목적지 정보 불러오기 테스트")
    class GetPlaceDetailsInfo {

        @Test
        @DisplayName("성공")
        void getPlaceDetailsInfoSuccess() {
            // given
            List<Place> places = List.of(place1, place2);

            TripDayPlace tripDayPlace = TripDayPlace.builder()
                    .day(1)
                    .places(places)
                    .build();

            given(tripDayPlaceService.readByIdSortedByOrder(tripDayPlaceId)).willReturn(Optional.of(tripDayPlace));

            // when
            List<TripPlaceRes.PlaceDetails> result = tripPlaceQueryService.getPlaceDetailsInfo(tripDayPlaceId);

            // then
            assertEquals(2, result.size());
            assertEquals("목적지1", result.get(0).name());
            assertEquals(0.0, result.get(0).latitude());
            assertEquals(2.2, result.get(1).latitude());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 일차 ID")
        void getPlaceDetailsInfoFail() {
            // given
            String tripDayPlaceId = "non-existent";
            given(tripDayPlaceService.readByIdSortedByOrder(tripDayPlaceId)).willReturn(Optional.empty());

            // when
            CustomException e = assertThrows(CustomException.class, () ->
                    tripPlaceQueryService.getPlaceDetailsInfo(tripDayPlaceId));

            assertEquals(TripErrorType.TRIP_PLACE_NOT_FOUND, e.getErrorType());
        }
    }

    @Test
    @DisplayName("일차별 요약 목록 조회 테스트")
    void getTripDaySummariesTest() {
        Image image = Image.builder()
                .url("https://image.com")
                .latitude(0.0)
                .longitude(1.0)
                .build();

        Place place = Place.builder()
                .id("place-id")
                .name("목적지")
                .latitude(1.0)
                .longitude(2.0)
                .placeType(PlaceCategory.RESTAURANT)
                .build();
        place.addImages(List.of(image));

        TripDayPlace tripDayPlace = TripDayPlace.builder()
                .tripId(tripId)
                .day(1)
                .places(List.of(place))
                .build();
        tripDayPlace.addUnmatchedImages(List.of(image));

        given(tripDayPlaceService.readTripDayPlaceSummariesByTripId(tripId))
                .willReturn(List.of(tripDayPlace));

        // when
        List<TripDaySummaryRes.DayDto> result = tripPlaceQueryService.getTripDaySummaries(tripId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).places()).hasSize(1);
        assertEquals(tripDayPlace.getId(), result.get(0).id());
        assertEquals(1, result.get(0).day());
    }
}
