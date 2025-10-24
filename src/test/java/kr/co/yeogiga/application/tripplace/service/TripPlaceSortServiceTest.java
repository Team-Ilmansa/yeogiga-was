package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReqLegacy;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
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

        given(redisRepository.getList(eq(listKey), eq(TripPlaceReqLegacy.StoredFormat.class)))
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

        List<TripPlaceReqLegacy.StoredFormat> input = List.of(
                new TripPlaceReqLegacy.StoredFormat("1", "PlaceA", "AddressA", 35.1, 128.1, PlaceCategory.RESTAURANT),
                new TripPlaceReqLegacy.StoredFormat("2", "PlaceB", "AddressB", 35.2, 128.2, PlaceCategory.TOURISM),
                new TripPlaceReqLegacy.StoredFormat("3", "PlaceC", "AddressC", 35.3, 128.3, PlaceCategory.LODGING)
        );

        given(redisRepository.getList(eq(listKey), eq(TripPlaceReqLegacy.StoredFormat.class)))
                .willReturn(input);

        // when
        tripPlaceSortService.sortDayTripPlaces(tripId, day);

        // then
        ArgumentCaptor<Collection<TripPlaceReqLegacy.StoredFormat>> captor =
                ArgumentCaptor.forClass(Collection.class);

        verify(redisRepository, times(1)).setListAll(eq(listKey), captor.capture());

        List<TripPlaceReqLegacy.StoredFormat> saved = new ArrayList<>(captor.getValue());
        assertEquals(PlaceCategory.LODGING, saved.get(2).placeCategory());
    }

    @Test
    @DisplayName("식당이 3개 이상 연속되지 않도록 배치된다")
    void preventConsecutiveRestaurantsTest() {
        // given
        String listKey = PlaceConstant.dayPlacesKey(tripId, day);

        List<TripPlaceReqLegacy.StoredFormat> input = List.of(
                new TripPlaceReqLegacy.StoredFormat("1", "A", "AddrA", 35.0, 128.0, PlaceCategory.RESTAURANT),
                new TripPlaceReqLegacy.StoredFormat("2", "B", "AddrB", 35.1, 128.1, PlaceCategory.RESTAURANT),
                new TripPlaceReqLegacy.StoredFormat("3", "C", "AddrC", 35.2, 128.2, PlaceCategory.RESTAURANT),
                new TripPlaceReqLegacy.StoredFormat("4", "D", "AddrD", 35.3, 128.3, PlaceCategory.LODGING),
                new TripPlaceReqLegacy.StoredFormat("5", "E", "AddrE", 35.4, 128.4, PlaceCategory.TOURISM)
        );

        given(redisRepository.getList(eq(listKey), eq(TripPlaceReqLegacy.StoredFormat.class)))
                .willReturn(input);

        // when
        tripPlaceSortService.sortDayTripPlaces(tripId, day);

        // then
        ArgumentCaptor<Collection<TripPlaceReqLegacy.StoredFormat>> captor =
                ArgumentCaptor.forClass(Collection.class);
        verify(redisRepository, times(1)).setListAll(eq(listKey), captor.capture());
        List<TripPlaceReqLegacy.StoredFormat> saved = new ArrayList<>(captor.getValue());

        // 식당 카테고리가 3번 이상 연속하지 않는지 확인
        int maxConsecutiveRestaurants = 0;
        int currentCount = 0;
        for (TripPlaceReqLegacy.StoredFormat place : saved) {
            if (PlaceCategory.RESTAURANT == place.placeCategory()) {
                currentCount++;
                maxConsecutiveRestaurants = Math.max(maxConsecutiveRestaurants, currentCount);
            } else {
                currentCount = 0;
            }
        }

        assertTrue(maxConsecutiveRestaurants <= 2);
    }
}
