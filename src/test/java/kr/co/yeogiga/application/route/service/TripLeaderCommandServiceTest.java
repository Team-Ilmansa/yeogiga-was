package kr.co.yeogiga.application.route.service;

import kr.co.yeogiga.application.route.dto.RouteDto;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripLeaderCommandServiceTest {

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private TripLeaderCommandService tripLeaderCommandService;

    private final Long tripId = 1L;
    private final int day = 1;
    private final double latitude = 37.000;
    private final double longitude = 127.000;

    @Test
    @DisplayName("이전 위치와 다르면 새로운 위치 추가")
    void storeLeaderRouteAddNewWhenLocationIsDifferent() {
        // given
        RouteDto last = RouteDto.toStoredFormat(37.000, 140.000);

        given(redisRepository.getLastFromList(anyString(), eq(RouteDto.class)))
                .willReturn(last);

        // when
        tripLeaderCommandService.storeLeaderRouteInRedis(tripId, day, latitude, longitude);

        // then
        verify(redisRepository).setList(anyString(), any());
        verify(redisRepository, never()).setValueInList(anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("이전 위치와 거의 같으면 덮어쓰기 수행")
    void storeLeaderRouteUpdateLastWhenLocationIsSimilar() {
        // given
        RouteDto last = new RouteDto(37.000000, 127.00001, LocalDateTime.now());

        given(redisRepository.getLastFromList(anyString(), eq(RouteDto.class)))
                .willReturn(last);

        // when
        tripLeaderCommandService.storeLeaderRouteInRedis(tripId, day, latitude, longitude);

        // then
        verify(redisRepository, times(1)).setValueInList(anyString(), eq(-1L), any());
        verify(redisRepository, never()).setList(anyString(), any());
    }
}
