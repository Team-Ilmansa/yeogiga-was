package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
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
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 일반 회원가입 메서드
     *
     * @param request           회원가입 요청 dto(username, password, email, nickname)
     * @throws CustomException  UserErrorType.ALREADY_EXISTS_USERNAME 이미 존재하는 아이디
     */
    @Transactional
    public void signUp(SignUpDto.Request request) {
        if (userService.existsIncludeDeletedByUsername(request.username())) {
            throw new CustomException(AuthErrorType.ALREADY_USED_USERNAME); // swagger 문서 수정 필요 / 테스트 코드 수정 필요
        }

        if (userService.existsIncludeDeletedByNickname(request.nickname())) {
            throw new CustomException(AuthErrorType.ALREADY_USED_NICKNAME);
        }

        User newUser = request.toEntity(passwordEncoder.encode(request.password()));

        newUser.upgradeRoleToUser();
        userService.save(newUser);
    }

    /**
     * 일반 로그인 메서드
     *
     * @param request               로그인 요청 dto(username, password)
     * @throws CustomException      AuthErrorType.AUTHENTICATION_FAIL 아이디 및 비밀번호 불일치
     * @return                      토큰(accessToken, refreshToken)
     */
    @Transactional(readOnly = true)
    public TokenDto signIn(SignInDto.Request request) {
        User user = userService.readIncludeDeletedUserByUsername(request.username())
                .orElseThrow(() -> new CustomException(AuthErrorType.AUTHENTICATION_FAIL));

        if (Objects.nonNull(user.getDeletedAt())) {
            user.revertWithdrawal();
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(AuthErrorType.AUTHENTICATION_FAIL);
        }

        return jwtService.generateToken(user.getUsername(), user.getNickname(), user.getId());
    }

    /**
     * 토큰 재발급 메서드
     *
     * @param refreshToken  리프레시 토큰
     * @return              신규 발급 토큰(accessToken, refreshToken)
     */
    public TokenDto reissueToken(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);

        if (!refreshTokenService.exists(userId)) {
            throw new CustomException(AuthErrorType.REFRESH_TOKEN_EXPIRED);
        }

        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        return jwtService.generateToken(user.getUsername(), user.getNickname(), user.getId());
    }

    /**
     * 로그아웃 메서드
     *
     * @param refreshToken  리프레시 토큰
     */
    public void signOut(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);
        refreshTokenService.delete(userId);
    }

    @Transactional(readOnly = true)
    public void checkDuplicatedUsername(String username) {
        if (userService.existsIncludeDeletedByUsername(username)) {
            throw new CustomException(AuthErrorType.ALREADY_USED_USERNAME);
        }
    }

    @Transactional(readOnly = true)
    public void checkDuplicatedNickname(String nickname) {
        if (userService.existsIncludeDeletedByNickname(nickname)) {
            throw new CustomException(AuthErrorType.ALREADY_USED_NICKNAME);
        }
    }
}
