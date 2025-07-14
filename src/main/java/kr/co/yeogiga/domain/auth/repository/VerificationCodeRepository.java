package kr.co.yeogiga.domain.auth.repository;

import java.util.Optional;

public interface VerificationCodeRepository {
    void save(String email, String code);
    
    Optional<String> getCode(String email);
    
    Long getExpire(String email);
    
    void delete(String email);
}
