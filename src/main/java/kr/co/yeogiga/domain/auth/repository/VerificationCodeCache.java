package kr.co.yeogiga.domain.auth.repository;

import kr.co.yeogiga.infrastructure.mail.constant.MailConstant;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VerificationCodeCache implements VerificationCodeRepository {
    private final RedisRepository redisRepository;
    
    private final Duration DURATION = Duration.ofMinutes(3);
    
    @Override
    public void save(String email, String code) {
        String key = MailConstant.formatVerificationCodePrefix(email);
        redisRepository.set(key, code, DURATION);
    }
    
    @Override
    public Optional<String> getCode(String email) {
        String key = MailConstant.formatVerificationCodePrefix(email);
        return Optional.ofNullable((String) redisRepository.get(key));
    }
    
    @Override
    public void delete(String email) {
        String key = MailConstant.formatVerificationCodePrefix(email);
        redisRepository.del(key);
    }
}
