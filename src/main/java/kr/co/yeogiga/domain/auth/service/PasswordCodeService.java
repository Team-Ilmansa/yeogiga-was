package kr.co.yeogiga.domain.auth.service;

import kr.co.yeogiga.domain.auth.repository.PasswordCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordCodeService {
    private final PasswordCodeRepository passwordCodeRepository;
    
    public void save(String email, String code, int expiration) {
        passwordCodeRepository.save(email, code, expiration);
    }
    
    public String getCode(String email) {
        return passwordCodeRepository.getCode(email);
    }
    
    public boolean existsCode(String email) {
        return passwordCodeRepository.existsCode(email);
    }
    
    public void del(String email) {
        passwordCodeRepository.del(email);
    }
}
