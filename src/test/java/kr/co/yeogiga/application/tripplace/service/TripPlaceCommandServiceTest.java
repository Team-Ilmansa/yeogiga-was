package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripPlaceCommandServiceTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @InjectMocks
    private TripPlaceCommandService tripPlaceCommandService;

    private final String tripDayPlaceId = "tripDayPlaceId";

    @Nested
    @DisplayName("새로운 목적지 추가 테스트")
    class AddNewsPlaceTest {

        @Captor
        private ArgumentCaptor<Place> placeCaptor;

        @Test
        @DisplayName("가장 처음 추가되는 경우 - 기존에 목적지 없는 상태")
        void addPlaceFirst() {
            // given
            given(tripDayPlaceService.readMaxOrderById(tripDayPlaceId)).willReturn(0.0);
            TripPlaceReq.Request request = TripPlaceReq.Request.builder()
                    .name("목적지1")
                    .latitude(0.0)
                    .longitude(0.0)
                    .placeType(PlaceCategory.RESTAURANT)
                    .build();

            // when
            tripPlaceCommandService.addNewPlace(tripDayPlaceId, request);

            // then
            verify(tripDayPlaceService).savePlace(eq(tripDayPlaceId), placeCaptor.capture());

            Place captured = placeCaptor.getValue();
            assertEquals(10.0, captured.getOrder());
        }

        @Test
        @DisplayName("가장 뒤에 추가되는 경우")
        void addPlaceBack() {
            // given
            given(tripDayPlaceService.readMaxOrderById(tripDayPlaceId)).willReturn(20.0);
            TripPlaceReq.Request request = TripPlaceReq.Request.builder()
                    .name("목적지1")
                    .latitude(0.0)
                    .longitude(0.0)
                    .placeType(PlaceCategory.RESTAURANT)
                    .build();

            // when
            tripPlaceCommandService.addNewPlace(tripDayPlaceId, request);

            // then
            verify(tripDayPlaceService).savePlace(eq(tripDayPlaceId), placeCaptor.capture());

            Place captured = placeCaptor.getValue();
            assertEquals(30.0, captured.getOrder());
        }

    }


    @Nested
    @DisplayName("목적지 정렬 테스트")
    class ReorderPlacesTest {

        @Test
        @DisplayName("정렬 성공")
        void reorderSuccess() {
            // given
            List<Place> places = List.of(
                    Place.builder().id("id1").name("목적지1").latitude(0.0).longitude(0.0).placeType(PlaceCategory.RESTAURANT).order(10.0).build(),
                    Place.builder().id("id2").name("목적지2").latitude(0.0).longitude(0.0).placeType(PlaceCategory.RESTAURANT).order(20.0).build(),
                    Place.builder().id("id3").name("목적지3").latitude(0.0).longitude(0.0).placeType(PlaceCategory.RESTAURANT).order(30.0).build()
            );
            TripDayPlace tripDayPlace = TripDayPlace.builder().day(1).places(places).build();
            TripPlaceReq.ReorderRequest reorderRequest = new TripPlaceReq.ReorderRequest(List.of("id3", "id1", "id2"));

            given(tripDayPlaceService.readById(tripDayPlaceId)).willReturn(Optional.of(tripDayPlace));

            // when
            tripPlaceCommandService.reorderPlaces(tripDayPlaceId, reorderRequest);

            // then
            assertEquals(places.get(2).getOrder(), 10.0);
            assertEquals(places.get(0).getOrder(), 20.0);
            assertEquals(places.get(1).getOrder(), 30.0);
            verify(tripDayPlaceService, times(1)).save(any());
        }

        @Test
        @DisplayName("실패 - 일차 없음")
        void reorderFailDayPlaceNotFound() {
            // given
            given(tripDayPlaceService.readById(tripDayPlaceId)).willReturn(Optional.empty());

            TripPlaceReq.ReorderRequest reorderRequest = new TripPlaceReq.ReorderRequest(List.of("a"));

            // when
            CustomException e = assertThrows(CustomException.class, () ->
                    tripPlaceCommandService.reorderPlaces(tripDayPlaceId, reorderRequest));

            // then
            assertEquals(TripErrorType.TRIP_PLACE_NOT_FOUND, e.getErrorType());
        }
    }

    @Test
    @DisplayName("목적지 방문 여부 변경 성공")
    void markPlaceAsVisitedSuccess() {
        // given
        String placeId = "place-id";
        boolean isVisited = true;

        // when
        tripPlaceCommandService.markPlaceAsVisited(tripDayPlaceId, placeId, isVisited);

        // then
        verify(tripDayPlaceService, times(1)).updatePlaceVisited(tripDayPlaceId, placeId, isVisited);
    }

    @Test
    @DisplayName("삭제 성공")
    void deletePlaceSuccess() {
        // given

        // when
        tripPlaceCommandService.deletePlace(tripDayPlaceId, "placeId");

        // then
        verify(tripDayPlaceService, times(1)).deletePlace(tripDayPlaceId, "placeId");
    }
}
