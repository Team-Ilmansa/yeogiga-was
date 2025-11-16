package kr.co.yeogiga.domain.auth.repository;

import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class PasswordCodeCache implements PasswordCodeRepository {
    private final RedisRepository redisRepository;
    private final Duration DURATION = Duration.ofMinutes(3);
    private final String PASSWORD_CODE_KEY_FORMAT = "password-code:%s";
    
    @Override
    public void save(String email, String code) {
        redisRepository.set(getPasswordCodeKey(email), code, DURATION);
    }
    
    @Override
    public String getCode(String email) {
        return (String) redisRepository.get(getPasswordCodeKey(email));
    }
    
    @Override
    public void del(String email) {
        redisRepository.del(getPasswordCodeKey(email));
    }
    
    private String getPasswordCodeKey(String email) {
        return PASSWORD_CODE_KEY_FORMAT.formatted(email);
    }
}
