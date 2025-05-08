package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
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

import java.util.Collections;
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
    @DisplayName("임시 저장소 목적지 테스트")
    class TempPlaceTest {

        private final String placeId = "place-id";

        @Test
        @DisplayName("임시 저장소에 목적지 추가 성공")
        void addTempPlaceSuccess() {
            // given
            TripPlaceReq.Request request = TripPlaceReq.Request.builder()
                    .name("목적지1")
                    .latitude(0.0)
                    .longitude(0.0)
                    .placeType("카페")
                    .build();

            // when
            tripPlaceEditingService.addTempPlace(tripId, request);

            // then
            verify(redisRepository, times(1)).setList(anyString(), any());
        }

        @Test
        @DisplayName("임시 저장소 목적지 목록 조회 성공")
        void getTempPlacesSuccess() {
            // given
            TripPlaceReq.StoredFormat storedPlace = new TripPlaceReq.StoredFormat(
                    placeId, "목적지1", 0.0, 0.0, "식당"
            );

            String tempListKey = PlaceConstant.tempListKey(tripId);
            given(redisRepository.getList(eq(tempListKey), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(List.of(storedPlace));

            // when
            List<TripPlaceReq.StoredFormat> result = tripPlaceEditingService.getTempPlaces(tripId);

            // then
            assertEquals(1, result.size());
            assertEquals("목적지1", result.get(0).name());
            assertEquals("식당", result.get(0).placeCategory());
        }

        @Test
        @DisplayName("임시 저장소에서 목적지 삭제 성공")
        void deleteTempPlaceSuccess() {
            // given
            TripPlaceReq.StoredFormat storedPlace = new TripPlaceReq.StoredFormat(
                    placeId, "목적지1", 0.0, 0.0, "식당"
            );

            String tempListKey = PlaceConstant.tempListKey(tripId);
            given(redisRepository.getList(eq(tempListKey), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(List.of(storedPlace));

            // when
            tripPlaceEditingService.deleteTempPlace(tripId, placeId);

            // then
            verify(redisRepository).removeFromList(eq(tempListKey), eq(storedPlace));
        }

        @Test
        @DisplayName("임시 저장소에서 목적지 삭제 실패 - 존재하지 않음")
        void deleteTempPlaceNotFound() {
            // given
            String key = PlaceConstant.tempListKey(tripId);
            given(redisRepository.getList(eq(key), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(List.of());

            // when
            assertDoesNotThrow(() -> tripPlaceEditingService.deleteTempPlace(tripId, placeId));

            // then
            verify(redisRepository, never()).removeFromList(anyString(), any());
        }
    }

    @Nested
    @DisplayName("일정에 목적지 담기 테스트")
    class AssignPlaceToDayTest {

        private final String placeId = "place-id";

        @Test
        @DisplayName("성공")
        void addPlaceInEditingSuccess() {
            // given
            String tempListKey = PlaceConstant.tempListKey(tripId);
            List<TripPlaceReq.StoredFormat> mockPlaces = List.of(
                    new TripPlaceReq.StoredFormat("place-id", "목적지1", 33.123, 126.456, PlaceCategory.CAFE.getGroupName())
            );
            given(redisRepository.getList(eq(tempListKey), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(mockPlaces);

            String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);
            given(redisRepository.existsInSet(eq(dayPlaceSetKey), anyString())).willReturn(false);

            // when
            tripPlaceEditingService.assignPlaceToDay(tripId, day, placeId);

            // then
            verify(redisRepository, times(1)).setList(anyString(), any(TripPlaceReq.StoredFormat.class));
            verify(redisRepository, times(1)).addToSet(eq(dayPlaceSetKey), anyString());
        }

        @Test
        @DisplayName("실패 - 담겨져 있지 않은 목적지")
        void addPlaceInEditingFailNotFoundPlace() {
            String tempListKey = PlaceConstant.tempListKey(tripId);
            given(redisRepository.getList(eq(tempListKey), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(Collections.emptyList());


            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    tripPlaceEditingService.assignPlaceToDay(tripId, day, placeId)
            );

            // then
            assertEquals(TripErrorType.NOT_FOUND_TEMP_PLACE, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 중복")
        void addPlaceInEditingFailAlreadyAdded() {
            // given
            String tempListKey = PlaceConstant.tempListKey(tripId);
            List<TripPlaceReq.StoredFormat> mockPlaces = List.of(
                    new TripPlaceReq.StoredFormat("place-id", "목적지1", 33.123, 126.456, PlaceCategory.CAFE.getGroupName())
            );
            given(redisRepository.getList(eq(tempListKey), eq(TripPlaceReq.StoredFormat.class)))
                    .willReturn(mockPlaces);

            String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);
            given(redisRepository.existsInSet(eq(dayPlaceSetKey), anyString())).willReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    tripPlaceEditingService.assignPlaceToDay(tripId, day, placeId)
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
            tripPlaceEditingService.deleteAssignedPlace(tripId, day, placeId);

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
            assertDoesNotThrow(() -> tripPlaceEditingService.deleteAssignedPlace(tripId, day, placeId));

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
        List<TripPlaceReq.StoredFormat> result = tripPlaceEditingService.getAssignedPlaces(tripId, day);

        // then
        assertEquals(1, result.size());
        assertEquals("목적지1", result.get(0).name());
        assertEquals(PlaceCategory.CAFE.getGroupName(), result.get(0).placeCategory());
    }
}
