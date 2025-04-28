package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;

    public TokenDto reissueToken(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);

        if (!refreshTokenService.exists(userId)) {
            throw new CustomException(AuthErrorType.REFRESH_TOKEN_EXPIRED);
        }

        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        TokenDto reissuedToken = jwtService.generateToken(user.getUsername(), user.getNickname(), user.getId());
        refreshTokenService.save(userId, reissuedToken.refreshToken());

        return reissuedToken;
    }

    public void signOut(String refreshToken) {
        Long userId = jwtService.extractUserId(refreshToken);
        refreshTokenService.delete(userId);
    }
}
