package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDeletionProcessor {
    private final UserService userService;

    /**
     * 탈퇴 보류 기간 후 사용자 정보 완전 삭제 메서드
     * - Soft Delete 정책에 따른 탈퇴 7일 이후 사용자 정보 삭제
     * - 사용자 관련 정보 일괄 삭제
     */
    @Transactional
    public void process() {
        LocalDate dateBeforeOneWeek = LocalDate.now().minusDays(7);
        List<Long> userIds = userService.readDeletedUserIdBefore(dateBeforeOneWeek);

        if (userIds.isEmpty()) {
            return;
        }

        /* TODO: 차후 연관 데이터 삭제 예정 */
        userService.deleteHardAllByIds(userIds);
    }
}
