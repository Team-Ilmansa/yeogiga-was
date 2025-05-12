package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.auth.service.RefreshTokenService;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.application.user.dto.UserInfoRes;
import kr.co.yeogiga.application.user.dto.UserInfoUpdateReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

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

    /**
     * 회원 정보 수정 메서드
     *
     * @param userId                사용자 ID
     * @param userInfoUpdateReq     사용자 정보 갱신 요청 dto(nickname, email)
     *
     * @throws CustomException      UserErrorType.ALREADY_USED_NICKNAME 동일한 닉네임 사용자가 존재하는 경우
     * @throws CustomException      UserErrorType.SAME_NICKNAME         기존과 동일한 닉네임의 경우
     * @throws CustomException      UserErrorType.SAME_EMAIL            기존과 동일한 이메일의 경우
     */
    @Transactional
    public void updateUserInfo(Long userId, UserInfoUpdateReq userInfoUpdateReq) {
        String nickname = userInfoUpdateReq.nickname();
        String email = userInfoUpdateReq.email();

        Optional<User> foundUser = userService.readIncludeDeletedUserByNickname(nickname);

        if (foundUser.isPresent() && !foundUser.get().getId().equals(userId)) {
            throw new CustomException(UserErrorType.ALREADY_USED_NICKNAME);
        }

        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        if (user.getNickname().equals(nickname)) {
            throw new CustomException(UserErrorType.SAME_NICKNAME);
        }

        if (user.getEmail().equals(email)) {
            throw new CustomException(UserErrorType.SAME_EMAIL);
        }

        user.updateUserInfo(nickname, email);
    }
}
