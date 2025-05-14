package kr.co.yeogiga.common.jwt;

import io.jsonwebtoken.Claims;
import kr.co.yeogiga.application.auth.type.LoginType;
import kr.co.yeogiga.common.jwt.dto.JwtClaims;
import kr.co.yeogiga.infrastructure.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHelper {
    private final TokenBuilder tokenBuilder;
    private final TokenParser tokenParser;
    private final JwtProperties jwtProperties;

    public String generateAccessToken(String nickname, Long userId, LoginType loginType) {
        return tokenBuilder.build(String.valueOf(userId), generateClaims(nickname, userId, loginType), jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String nickname, Long userId, LoginType loginType) {
        return tokenBuilder.build(String.valueOf(userId), generateClaims(nickname, userId, loginType), jwtProperties.getRefreshTokenExpiration());
    }

    public JwtClaims parseClaims(String token) {
        Claims claims = tokenParser.parseClaims(token);

        return JwtClaims.builder()
                .nickname(claims.get("nickname", String.class))
                .userId(claims.get("userId", Long.class))
                .build();
    }

    private Map<String, Object> generateClaims(String nickname, Long userId, LoginType loginType) {
        return Map.of("nickname", nickname,
                      "userId", userId,
                      "loginType", loginType);

    }
}