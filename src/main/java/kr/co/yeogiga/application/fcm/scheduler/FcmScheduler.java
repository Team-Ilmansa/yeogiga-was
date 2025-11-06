package kr.co.yeogiga.application.fcm.scheduler;

import kr.co.yeogiga.application.fcm.service.TripTokenPushProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmScheduler {
    private final TripTokenPushProcessor tripTokenPushProcessor;

    /**
     * Silent Push 전송을 위한 스케쥴러
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void sendSilentPushToActiveTripsJob() {
        tripTokenPushProcessor.process();
    }
}
