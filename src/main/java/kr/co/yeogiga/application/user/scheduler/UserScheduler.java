package kr.co.yeogiga.application.user.scheduler;

import kr.co.yeogiga.application.user.service.UserDeletionProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserScheduler {
    private final UserDeletionProcessor userDeletionProcessor;

    @Scheduled(cron = "0 0 0 * * *")
    public void runUserCleanUpJob() {
        userDeletionProcessor.process();
    }
}
