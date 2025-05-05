package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    /**
     * 사용자 비밀번호 갱신 메서드
     *
     * @param userId            사용자 ID
     * @param passwordReq       비밀번호 갱신 요청 dto(password)
     * @throws CustomException  UserErrorType.SAME_PASSWORD - 기존 비밀번호와 동일할 경우
     */
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateReq passwordReq) {
        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        if (passwordEncoder.matches(passwordReq.password(), user.getPassword())) {
            throw new CustomException(UserErrorType.SAME_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(passwordReq.password()));
    }
}
