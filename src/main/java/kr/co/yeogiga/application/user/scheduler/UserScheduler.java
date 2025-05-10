package kr.co.yeogiga.application.user.scheduler;

import kr.co.yeogiga.application.user.service.UserDeletionProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserScheduler {
    private final UserDeletionProcessor userDeletionProcessor;


    /* TODO: 개발 환경 내 유저 데이터 삭제 로직 실행 보류로 인한 차후 변경 예정
    @Scheduled(cron = "0 0 0 * * *")
    public void runUserCleanUpJob() {
        userDeletionProcessor.process();
    }*/
}
