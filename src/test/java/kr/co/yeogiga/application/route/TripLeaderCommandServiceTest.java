package kr.co.yeogiga.application.route;

import kr.co.yeogiga.application.route.dto.RouteReq;
import kr.co.yeogiga.application.route.service.TripLeaderCommandService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripLeaderCommandServiceTest {

    @Mock
    private RedisRepository redisRepository;

    @InjectMocks
    private TripLeaderCommandService tripLeaderCommandService;

    private final Long tripId = 1L;
    private final int day = 1;

    private final RouteReq.Request request =
            new RouteReq.Request(35.000000, 129.000000);

    @Test
    @DisplayName("이전 위치와 다르면 새로운 위치 추가")
    void storeLeaderRouteAddNewWhenLocationIsDifferent() {
        // given
        RouteReq.StoredFormat last = new RouteReq.StoredFormat(37.000000, 127.000000, LocalDateTime.now());

        given(redisRepository.getLastFromList(anyString(), eq(RouteReq.StoredFormat.class)))
                .willReturn(last);

        // when
        tripLeaderCommandService.storeLeaderRouteInRedis(tripId, day, request);

        // then
        verify(redisRepository).setList(anyString(), any());
        verify(redisRepository, never()).setValueInList(anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("이전 위치와 거의 같으면 덮어쓰기 수행")
    void storeLeaderRouteUpdateLastWhenLocationIsSimilar() {
        // given
        RouteReq.StoredFormat last = new RouteReq.StoredFormat(35.000000, 129.000000, LocalDateTime.now());

        given(redisRepository.getLastFromList(anyString(), eq(RouteReq.StoredFormat.class)))
                .willReturn(last);

        // when
        tripLeaderCommandService.storeLeaderRouteInRedis(tripId, day, request);

        // then
        verify(redisRepository).setValueInList(anyString(), eq(-1L), any());
        verify(redisRepository, never()).setList(anyString(), any());
    }
}
