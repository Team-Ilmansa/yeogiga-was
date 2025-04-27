package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.common.jwt.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtHelper jwtHelper;

    public TokenDto generateToken(String username, String nickname, Long userId) {
        return TokenDto.of(
                jwtHelper.generateAccessToken(username, nickname, userId),
                jwtHelper.generateRefreshToken(username, nickname, userId));
    }

    public Long extractUserId(String token) {
        return jwtHelper.parseClaims(token)
                .userId();
    }
}