package kr.co.yeogiga.application.route.service;

import kr.co.yeogiga.application.route.dto.RouteRes;
import kr.co.yeogiga.domain.triproute.entity.Route;
import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import kr.co.yeogiga.domain.triproute.service.TripRouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TripRouteQueryServiceTest {

    @Mock
    private TripRouteService tripRouteService;

    @InjectMocks
    private TripRouteQueryService tripRouteQueryService;

    @Test
    @DisplayName("여행 루트 조회 테스트")
    void getTripRoutesTest() {
        // given
        Long tripId = 1L;
        Route route1 = Route.builder().latitude(0.0).longitude(1.1).build();
        Route route2 = Route.builder().latitude(2.2).longitude(3.3).build();

        TripRoute tripRoute1 = TripRoute.builder()
                .tripId(tripId)
                .day(1)
                .routes(List.of(route1))
                .build();

        TripRoute tripRoute2 = TripRoute.builder()
                .tripId(tripId)
                .day(2)
                .routes(List.of(route2))
                .build();

        given(tripRouteService.readByTripId(tripId)).willReturn(List.of(tripRoute1, tripRoute2));

        // when
        List<RouteRes> result = tripRouteQueryService.getTripRoutes(tripId);

        // then
        assertThat(result).hasSize(2);
        assertEquals(route1.getLatitude(), result.get(0).routes().get(0).getLatitude());
        assertEquals(route2.getLatitude(), result.get(1).routes().get(0).getLatitude());
    }
}
