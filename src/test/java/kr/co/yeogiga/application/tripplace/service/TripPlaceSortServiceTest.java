package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TripPlaceSortServiceTest {

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private TripPlaceSortService tripPlaceSortService;

    private final Long tripId = 1L;
    private final int day = 1;

    @Test
    @DisplayName("장소가 비어있으면 정렬을 수행하지 않는다")
    void emptyPlacesTest() {
        // given
        String listKey = PlaceConstant.dayPlacesKey(tripId, day);

        given(redisRepository.getList(eq(listKey), eq(TripPlaceReq.StoredFormat.class)))
                .willReturn(Collections.emptyList());

        // when
        tripPlaceSortService.sortDayTripPlaces(tripId, day);

        // then
        verify(redisRepository, never()).setList(eq(listKey), any());
    }

    @Test
    @DisplayName("숙소는 항상 마지막에 배치된다")
    void lodgingAtLastTest() {
        // given
        String listKey = PlaceConstant.dayPlacesKey(tripId, day);

        List<TripPlaceReq.StoredFormat> input = List.of(
                new TripPlaceReq.StoredFormat("1", "PlaceA", 35.1, 128.1, "식당"),
                new TripPlaceReq.StoredFormat("2", "PlaceB", 35.2, 128.2, "관광지"),
                new TripPlaceReq.StoredFormat("3", "PlaceC", 35.3, 128.3, "숙소")
        );

        given(redisRepository.getList(eq(listKey), eq(TripPlaceReq.StoredFormat.class)))
                .willReturn(input);

        // when
        tripPlaceSortService.sortDayTripPlaces(tripId, day);

        // then
        ArgumentCaptor<TripPlaceReq.StoredFormat> captor = ArgumentCaptor.forClass(TripPlaceReq.StoredFormat.class);
        verify(redisRepository, times(3)).setList(eq(listKey), captor.capture());

        List<TripPlaceReq.StoredFormat> saved = captor.getAllValues();
        assertEquals("숙소", saved.get(2).placeCategory(), "마지막 장소는 숙소여야 함");
    }

    @Test
    @DisplayName("식당이 3개 이상 연속되지 않도록 배치된다")
    void preventConsecutiveRestaurantsTest() {
        // given
        String listKey = PlaceConstant.dayPlacesKey(tripId, day);

        List<TripPlaceReq.StoredFormat> input = List.of(
                new TripPlaceReq.StoredFormat("1", "A", 35.0, 128.0, "식당"),
                new TripPlaceReq.StoredFormat("2", "B", 35.1, 128.1, "식당"),
                new TripPlaceReq.StoredFormat("3", "C", 35.2, 128.2, "식당"),
                new TripPlaceReq.StoredFormat("4", "D", 35.3, 128.3, "숙소"),
                new TripPlaceReq.StoredFormat("5", "E", 35.4, 128.4, "관광지")
        );

        given(redisRepository.getList(eq(listKey), eq(TripPlaceReq.StoredFormat.class)))
                .willReturn(input);

        // when
        tripPlaceSortService.sortDayTripPlaces(tripId, day);

        // then
        ArgumentCaptor<TripPlaceReq.StoredFormat> captor = ArgumentCaptor.forClass(TripPlaceReq.StoredFormat.class);
        verify(redisRepository, times(5)).setList(eq(listKey), captor.capture());
        List<TripPlaceReq.StoredFormat> saved = captor.getAllValues();

        // 식당 카테고리가 3번 이상 연속하지 않는지 확인
        int maxConsecutiveRestaurants = 0;
        int currentCount = 0;
        for (TripPlaceReq.StoredFormat place : saved) {
            if ("식당".equals(place.placeCategory())) {
                currentCount++;
                maxConsecutiveRestaurants = Math.max(maxConsecutiveRestaurants, currentCount);
            } else {
                currentCount = 0;
            }
        }

        assertTrue(maxConsecutiveRestaurants <= 2);
    }
}
