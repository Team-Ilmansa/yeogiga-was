package kr.co.yeogiga.common.jwt;

import io.jsonwebtoken.Claims;
import kr.co.yeogiga.common.jwt.dto.JwtClaims;
import kr.co.yeogiga.infrastructure.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static kr.co.yeogiga.application.auth.constant.AuthConstants.TOKEN_TYPE;

@Component
@RequiredArgsConstructor
public class JwtHelper {
    private final TokenBuilder tokenBuilder;
    private final TokenParser tokenParser;
    private final JwtProperties jwtProperties;

    public static String resolveToken(String token) {
        return token.replace(TOKEN_TYPE, "").trim();
    }

    public String generateAccessToken(String username, String nickname, Long userId) {
        return tokenBuilder.build(username, generateClaims(nickname, userId), jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(String username, String nickname, Long userId) {
        return tokenBuilder.build(username, generateClaims(nickname, userId), jwtProperties.getRefreshTokenExpiration());
    }

    public JwtClaims parseClaims(String token) {
        Claims claims = tokenParser.parseClaims(token);

        return JwtClaims.builder()
                .nickname(claims.get("nickname", String.class))
                .username(claims.get("username", String.class))
                .userId(claims.get("userId", Long.class))
                .build();
    }

    private Map<String, Object> generateClaims(String nickname, Long userId) {
        return Map.of("nickname", nickname,
                      "userId", userId);

    }
}