package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.type.LoginType;
import kr.co.yeogiga.common.jwt.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtHelper jwtHelper;
    private final RefreshTokenService refreshTokenService;

    public TokenDto generateToken(String username, String nickname, Long userId, LoginType loginType) {
        String accessToken = jwtHelper.generateAccessToken(username, nickname, userId, loginType);
        String refreshToken = jwtHelper.generateRefreshToken(username, nickname, userId, loginType);
        refreshTokenService.save(userId, refreshToken);

        return TokenDto.of(accessToken, refreshToken);
    }

    public Long extractUserId(String token) {
        return jwtHelper.parseClaims(token)
                .userId();
    }
}