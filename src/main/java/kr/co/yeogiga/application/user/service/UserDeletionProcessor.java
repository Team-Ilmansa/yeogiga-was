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

    @Transactional
    public void process() {
        LocalDate dateBeforeOneWeek = LocalDate.now().minusDays(7);
        List<Long> userIds = userService.readDeletedUserIdBefore(dateBeforeOneWeek);

        if (userIds.isEmpty()) {
            return;
        }

        /* TODO: 차후 연관 데이터 삭제 예정*/
        userService.deleteHardAllByIds(userIds);
    }
}
