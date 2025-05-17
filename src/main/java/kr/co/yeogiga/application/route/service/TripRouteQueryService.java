package kr.co.yeogiga.application.route.service;

import kr.co.yeogiga.application.route.dto.RouteRes;
import kr.co.yeogiga.domain.triproute.service.TripRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripRouteQueryService {
    private final TripRouteService tripRouteService;

    /**
     * 여행의 루트(이동 경로)를 조회하는 메서드
     *
     * @param tripId 여행 ID
     * @return 여행의 이동 경로 DTO
     */
    public List<RouteRes> getTripRoutes(Long tripId) {
        return tripRouteService.readByTripId(tripId).stream()
                .map(RouteRes::from)
                .toList();
    }
}
