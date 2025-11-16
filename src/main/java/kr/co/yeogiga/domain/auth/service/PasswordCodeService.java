package kr.co.yeogiga.domain.auth.service;

import kr.co.yeogiga.domain.auth.repository.PasswordCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordCodeService {
    private final PasswordCodeRepository passwordCodeRepository;
    
    public void save(String email, String code) {
        passwordCodeRepository.save(email, code);
    }
    
    public String getCode(String email) {
        return passwordCodeRepository.getCode(email);
    }
}
