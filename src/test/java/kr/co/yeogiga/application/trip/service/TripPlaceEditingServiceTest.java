package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripPlaceReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripPlaceEditingServiceTest {

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private TripPlaceEditingService tripPlaceEditingService;

    private final Long tripId = 1L;
    private final int day = 1;

    @Nested
    @DisplayName("목적지 추가 테스트")
    class AddPlaceTest {

        private final TripPlaceReq.Request place = TripPlaceReq.Request.builder()
                .name("목적지1")
                .latitude(0.0)
                .longitude(0.0)
                .placeType("카페")
                .build();

        @Test
        @DisplayName("성공")
        void addPlaceInEditingSuccess() {
            // given
            String setKey = PlaceConstant.setKey(tripId, day);
            given(redisRepository.existsInSet(eq(setKey), anyString())).willReturn(false);

            // when
            tripPlaceEditingService.addPlace(tripId, day, place);

            // then
            verify(redisRepository, times(1)).setList(anyString(), any(TripPlaceReq.StoredFormat.class));
            verify(redisRepository, times(1)).addToSet(eq(setKey), anyString());
        }

        @Test
        @DisplayName("실패 - 중복")
        void addPlaceInEditingFailAlreadyAdded() {
            // given
            String setKey = PlaceConstant.setKey(tripId, day);
            given(redisRepository.existsInSet(eq(setKey), anyString())).willReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    tripPlaceEditingService.addPlace(tripId, day, place)
            );

            // then
            assertEquals(TripErrorType.ALREADY_ADDED_PLACE, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("목적지 삭제 테스트")
    class DeletePlaceTest {

        private final String placeId = "place-id";

        @Test
        @DisplayName("목적지 삭제 성공")
        void deletePlaceInEditingSuccess() {
            // given
            TripPlaceReq.StoredFormat place = new TripPlaceReq.StoredFormat(
                    placeId, "목적지1", 33.123, 126.456, PlaceCategory.RESTAURANT.getGroupName()
            );

            given(redisRepository.getList(anyString(), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(List.of(place));

            // when
            tripPlaceEditingService.deletePlace(tripId, day, placeId);

            // then
            verify(redisRepository, times(1)).removeFromList(anyString(), eq(place));
            verify(redisRepository, times(1)).removeFromSet(anyString(), anyString());
        }

        @Test
        @DisplayName("목적지 삭제 실패 - 존재하지 않음")
        void deletePlaceInEditingNotFound() {
            // given
            given(redisRepository.getList(anyString(), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(List.of());

            // when
            assertDoesNotThrow(() -> tripPlaceEditingService.deletePlace(tripId, day, placeId));

            // then
            verify(redisRepository, never()).removeFromList(anyString(), any());
            verify(redisRepository, never()).removeFromSet(anyString(), anyString());
        }
    }

    @Test
    @DisplayName("목적지 순서 수정 성공")
    void updatePlacesInEditingSuccess() {
        // given
        TripPlaceReq.Request place1 = TripPlaceReq.Request.builder()
                .name("목적지1")
                .latitude(0.0)
                .longitude(0.0)
                .placeType("카페")
                .build();

        TripPlaceReq.Request place2 = TripPlaceReq.Request.builder()
                .name("목적지2")
                .latitude(0.0)
                .longitude(0.0)
                .placeType("카페")
                .build();

        List<TripPlaceReq.Request> newPlaces = List.of(place2, place1);

        // when
        tripPlaceEditingService.updatePlaces(tripId, day, newPlaces);

        // then
        verify(redisRepository, times(2)).del(anyString());
        verify(redisRepository, times(2)).setList(anyString(), any(TripPlaceReq.StoredFormat.class));
        verify(redisRepository, times(2)).addToSet(anyString(), anyString());
    }

    @Test
    @DisplayName("편집 중인 여행 일정 조회 성공")
    void getPlacesInEditingSuccess() {
        // given
        List<TripPlaceReq.StoredFormat> mockPlaces = List.of(
                new TripPlaceReq.StoredFormat("place-id", "목적지1", 33.123, 126.456, PlaceCategory.CAFE.getGroupName())
        );

        given(redisRepository.getList(anyString(), eq(TripPlaceReq.StoredFormat.class))).willReturn(mockPlaces);

        // when
        List<TripPlaceReq.StoredFormat> result = tripPlaceEditingService.getPlaces(tripId, day);

        // then
        assertEquals(1, result.size());
        assertEquals("목적지1", result.get(0).name());
        assertEquals(PlaceCategory.CAFE.getGroupName(), result.get(0).placeCategory());
    }
}
