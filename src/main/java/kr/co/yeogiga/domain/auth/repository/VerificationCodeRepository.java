package kr.co.yeogiga.domain.auth.repository;

public interface VerificationCodeRepository {
    void save(String email, String code);
}
