package kr.co.yeogiga.application.trip.scheduler;

import kr.co.yeogiga.application.trip.service.TripCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TripScheduler {
    private final TripCommandService tripCommandService;

    @Scheduled(cron = "0 55 * * * *")
    public void runUpdateTravelStatusJob() {
        tripCommandService.updateTravelStatus(LocalDateTime.now().plusMinutes(5));
    }
}
