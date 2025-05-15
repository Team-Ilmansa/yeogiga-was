package kr.co.yeogiga.application.route.scheduler;

import kr.co.yeogiga.application.route.service.TripLeaderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RouteScheduler {
    private final TripLeaderCommandService tripLeaderCommandService;

    /**
     * Redis 내 여행 루트를 RDB로 로드하기 위한 스케쥴러
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void persistTripRoutesDailyJob() {
        tripLeaderCommandService.persistAllTripRoutes();
    }
}
