package kr.co.yeogiga.application.route.service;

import kr.co.yeogiga.application.route.dto.RouteReq;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.RouteConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripLeaderCommandService {
    private final RedisRepository redisRepository;

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
}
