package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

    @InjectMocks
    private TripPlaceSavingService tripPlaceSavingService;

    private final Long tripId = 1L;
    private final int lastDay = 2;

    @Test
    @DisplayName("여행 생성 완료 성공")
    void completeTripSuccess() {
        // given
        TripPlaceDto.StoredFormat place1 = new TripPlaceDto.StoredFormat(
                "id1", "장소1", 33.123, 126.456, "관광지"
        );
        TripPlaceDto.StoredFormat place2 = new TripPlaceDto.StoredFormat(
                "id2", "장소2", 33.789, 126.987, "식당"
        );

        when(redisRepository.getList(anyString(), eq(TripPlaceDto.StoredFormat.class)))
                .thenReturn(List.of(place1))
                .thenReturn(List.of(place2));

        // when
        tripPlaceSavingService.completeTrip(tripId, lastDay);

        // then
        verify(redisRepository, times(2)).getList(anyString(), eq(TripPlaceDto.StoredFormat.class));
        verify(tripDayPlaceService, times(1)).saveAll(any());
        verify(redisRepository, times(1)).del(PlaceConstant.listKey(tripId, 1));
        verify(redisRepository, times(1)).del(PlaceConstant.setKey(tripId, 1));
        verify(redisRepository, times(1)).del(PlaceConstant.listKey(tripId, 2));
        verify(redisRepository, times(1)).del(PlaceConstant.setKey(tripId, 2));
    }
}
