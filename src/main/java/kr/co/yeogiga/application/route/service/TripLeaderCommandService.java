package kr.co.yeogiga.application.route.service;

import kr.co.yeogiga.application.route.dto.RouteReq;
import kr.co.yeogiga.domain.triproute.entity.Route;
import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import kr.co.yeogiga.domain.triproute.service.TripRouteService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.RouteConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TripLeaderCommandService {
    private final RedisRepository redisRepository;
    private final TripRouteService tripRouteService;

    // (위도/경도) 비교 시 같은 위치로 간주할 오차 범위 (약 10~14m 정도)
    private static final double LOCATION_THRESHOLD = 0.0001;

    /**
     * 방장(TripLeader)의 이동 경로를 Redis에 저장하는 메서드
     * - 직전 저장 위치와 현재 위치가 거의 같으면 덮어쓰기 수행
     * - 위치가 다르면 새로운 위치로 추가 저장
     *
     * @param tripId   여행 ID
     * @param day      여행 일차
     * @param routeReq 클라이언트에서 보낸 위치 정보 (위도, 경도)
     */
    public void storeLeaderRouteInRedis(Long tripId, int day, RouteReq.Request routeReq) {
        String tripRouteKey = RouteConstant.TripRouteKey(tripId, day);

        RouteReq.StoredFormat routeStoredFormat = routeReq.toStoredFormat();

        RouteReq.StoredFormat lastRoute =
                redisRepository.getLastFromList(tripRouteKey, RouteReq.StoredFormat.class);

        // 마지막 위치와 현재 위치가 거의 같다면 → 시간만 갱신해서 덮어쓰기
        if (lastRoute != null && isSimilarLocation(lastRoute, routeStoredFormat)) {
            redisRepository.setValueInList(tripRouteKey, -1, routeStoredFormat);
            return;
        }

        redisRepository.setList(tripRouteKey, routeStoredFormat);
    }

    /**
     * 두 위치가 LOCATION_THRESHOLD 이내로 유사한지 판단하는 메서드
     *
     * @param prevRoute 이전 위치
     * @param nowRoute  현재 위치
     * @return 동일한 위치인지 판단 값
     */
    private boolean isSimilarLocation(RouteReq.StoredFormat prevRoute, RouteReq.StoredFormat nowRoute) {
        double latDiff = Math.abs(prevRoute.latitude() - nowRoute.latitude());
        double lonDiff = Math.abs(prevRoute.longitude() - nowRoute.longitude());

        return latDiff < LOCATION_THRESHOLD && lonDiff < LOCATION_THRESHOLD;
    }

    /**
     * Redis에 버퍼링된 TripRoute 데이터를 RDB에 로드하는 메서드.
     * - TRIP_ROUTE_KEY_PATTERN을 통한 Redis 내 키 패턴 조회
     * - 각 키의 루트 데이터를 TripRoute 엔티티로 변환
     * - RDB에 일괄 저장 후, 관련 Redis 키들을 삭제
     */
    public void persistAllTripRoutes() {
        Set<String> keys = redisRepository.getKeysByPattern(RouteConstant.TRIP_ROUTE_KEY_PATTERN);

        List<TripRoute> tripRoutes = new ArrayList<>();

        for (String key : keys) {
            TripRoute tripRoute = convertKeyToTripRoute(key);
            if (tripRoute == null) continue;

            tripRoutes.add(tripRoute);
        }

        tripRouteService.saveAll(tripRoutes);
        redisRepository.deleteKeys(keys.stream().toList());
    }

    /**
     * Redis 키 문자열을 파싱하여 TripRoute 도메인 엔티티로 변환하는 메서드
     * - Redis에 저장된 StoredFormat 리스트를 Route 리스트로 매핑
     *
     * @param key Redis에 저장된 경로 데이터 키
     * @return TripRoute 엔티티
     */
    private TripRoute convertKeyToTripRoute(String key) {
        String[] parts = key.split(":");
        Long tripId = Long.parseLong(parts[2]);
        int day = Integer.parseInt(parts[3]);

        List<RouteReq.StoredFormat> storedRoutes = redisRepository.getList(key, RouteReq.StoredFormat.class);

        if (storedRoutes == null || storedRoutes.isEmpty()) {
            return null;
        }

        List<Route> routes = storedRoutes.stream()
                .map(r -> Route.builder()
                        .latitude(r.latitude())
                        .longitude(r.longitude())
                        .time(r.time())
                        .build())
                .toList();

        return TripRoute.builder()
                .tripId(tripId)
                .day(day)
                .routes(routes)
                .build();
    }
}
