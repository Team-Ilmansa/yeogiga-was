package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.infrastructure.properties.JwtProperties;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.TokenConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProperties jwtProperties;
    private final RedisRepository redisRepository;

    public void save(Long userId, String token) {
        redisRepository.set(
                TokenConstant.parseRefreshTokenKey(userId),
                token,
                Duration.ofSeconds(jwtProperties.getRefreshTokenExpiration())
        );
    }

    public void delete(Long userId) {
        redisRepository.del(TokenConstant.parseRefreshTokenKey(userId));
    }

    public boolean exists(Long userId) {
        return redisRepository.existed(TokenConstant.parseRefreshTokenKey(userId));
    }
}
