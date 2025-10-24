package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReqLegacy;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
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
import static org.mockito.ArgumentMatchers.anyList;
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
    @DisplayName("일정에 목적지 담기 테스트")
    class AssignPlaceToDayTest {

        private final String placeId = "place-id";
        private final TripPlaceReqLegacy.Request request = TripPlaceReqLegacy.Request.builder()
                .name("목적지1")
                .latitude(0.0)
                .longitude(0.0)
                .placeType(PlaceCategory.RESTAURANT)
                .build();

        @Test
        @DisplayName("성공")
        void addPlaceInEditingSuccess() {
            // given
            String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);
            given(redisRepository.existsInSet(eq(dayPlaceSetKey), anyString())).willReturn(false);

            // when
            tripPlaceEditingService.assignPlaceToDay(tripId, day, request);

            // then
            verify(redisRepository, times(1)).setList(anyString(), any(TripPlaceReqLegacy.StoredFormat.class));
            verify(redisRepository, times(1)).addToSet(eq(dayPlaceSetKey), anyString());
        }

        @Test
        @DisplayName("실패 - 중복")
        void addPlaceInEditingFailAlreadyAdded() {
            // given
            String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);
            given(redisRepository.existsInSet(eq(dayPlaceSetKey), anyString())).willReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    tripPlaceEditingService.assignPlaceToDay(tripId, day, request)
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
            TripPlaceReqLegacy.StoredFormat place = new TripPlaceReqLegacy.StoredFormat(
                    placeId, "목적지1", "주소1", 33.123, 126.456, PlaceCategory.RESTAURANT
            );

            given(redisRepository.getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class)))
                    .willReturn(List.of(place));

            // when
            tripPlaceEditingService.deleteAssignedPlace(tripId, day, placeId);

            // then
            verify(redisRepository, times(1)).removeFromList(anyString(), eq(place));
            verify(redisRepository, times(1)).removeFromSet(anyString(), anyString());
        }

        @Test
        @DisplayName("목적지 삭제 실패 - 존재하지 않음")
        void deletePlaceInEditingNotFound() {
            // given
            given(redisRepository.getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class)))
                    .willReturn(List.of());

            // when
            assertDoesNotThrow(() -> tripPlaceEditingService.deleteAssignedPlace(tripId, day, placeId));

            // then
            verify(redisRepository, never()).removeFromList(anyString(), any());
            verify(redisRepository, never()).removeFromSet(anyString(), anyString());
        }
    }

    @Test
    @DisplayName("목적지 순서 수정 성공")
    void reorderPlacesInEditingSuccess() {
        // given
        List<TripPlaceReqLegacy.StoredFormat> storedPlace = List.of(
                new TripPlaceReqLegacy.StoredFormat("place1-id", "목적지1", "주소1", 33.1, 126.1, PlaceCategory.RESTAURANT),
                new TripPlaceReqLegacy.StoredFormat("place2-id", "목적지2", "주소2", 33.2, 126.2, PlaceCategory.RESTAURANT),
                new TripPlaceReqLegacy.StoredFormat("place3-id", "목적지3", "주소3", 33.3, 126.3, PlaceCategory.RESTAURANT)
        );

        TripPlaceReqLegacy.ReorderRequest request =
                new TripPlaceReqLegacy.ReorderRequest(List.of("place3-id", "place1-id", "place2-id"));

        String listKey = "trip:1:day:1:places";

        given(redisRepository.getList(listKey, TripPlaceReqLegacy.StoredFormat.class)).willReturn(storedPlace);

        // when
        tripPlaceEditingService.reorderPlaces(tripId, day, request);

        // then
        verify(redisRepository, times(2)).del(anyString());
        verify(redisRepository, times(1)).setListAll(anyString(), anyList());
        verify(redisRepository, times(3)).addToSet(anyString(), anyString());
    }

    @Test
    @DisplayName("편집 중인 여행 일정 조회 성공")
    void getPlacesInEditingSuccess() {
        // given
        List<TripPlaceReqLegacy.StoredFormat> mockPlaces = List.of(
                new TripPlaceReqLegacy.StoredFormat("place-id", "목적지1", "주소3", 33.123, 126.456, PlaceCategory.RESTAURANT)
        );

        given(redisRepository.getList(anyString(), eq(TripPlaceReqLegacy.StoredFormat.class))).willReturn(mockPlaces);

        // when
        List<TripPlaceRes.TempPlaceInfo> result = tripPlaceEditingService.getAssignedPlaces(tripId, day);

        // then
        assertEquals(1, result.size());
        assertEquals("목적지1", result.get(0).name());
        assertEquals(PlaceCategory.RESTAURANT.getLabel(), result.get(0).placeCategory());
    }
}
