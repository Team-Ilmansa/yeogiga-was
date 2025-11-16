package kr.co.yeogiga.domain.auth.repository;

public interface PasswordCodeRepository {
    void save(String email, String code);
    
    String getCode(String email);
}
