package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.auth.service.RefreshTokenService;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.application.user.dto.UserInfoRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    /**
     * 사용자 비밀번호 갱신 메서드
     *
     * @param userId            사용자 ID
     * @param passwordReq       비밀번호 갱신 요청 dto(originalPassword, newPassword)
     * @throws CustomException  UserErrorType.PASSWORD_MISMATCH - 기존 비밀번호가 불일치할 경우
     * @throws CustomException  UserErrorType.SAME_PASSWORD - 새로운 비밀번호가 기존 비밀번호와 동일할 경우
     */
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateReq passwordReq) {
        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        if (!passwordEncoder.matches(passwordReq.originalPassword(), user.getPassword())) {
            throw new CustomException(UserErrorType.PASSWORD_MISMATCH);
        }

        if (passwordEncoder.matches(passwordReq.newPassword(), user.getPassword())) {
            throw new CustomException(UserErrorType.SAME_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(passwordReq.newPassword()));
    }

    /**
     * 회원탈퇴 메서드
     *
     * @param userId            사용자 ID
     */
    @Transactional
    public void withdraw(Long userId) {
        User user = userService.readIncludeDeletedUserById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        if (Objects.nonNull(user.getDeletedAt())) {
            throw new CustomException(UserErrorType.ALREADY_WITHDRAW);
        }

        refreshTokenService.delete(userId);
        userService.deleteById(userId);
    }

    /**
     * 회원 정보 조회 메서드
     *
     * @param userId    사용자 ID
     * @return          UserInfoRes 사용자 정보
     *                  - 소셜 로그인 사용자 -> nickname, email
     *                  - 일반 로그인 사용자 -> nickname, email, username
     */
    @Transactional(readOnly = true)
    public UserInfoRes getUserInfo(Long userId) {
        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        return Objects.isNull(user.getPassword())
                ? UserInfoRes.fromSocialUser(user)
                : UserInfoRes.fromNormalUser(user);
    }
}
