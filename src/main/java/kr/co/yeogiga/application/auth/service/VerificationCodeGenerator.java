package kr.co.yeogiga.application.auth.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {
    private final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * 6자리 난수를 생성하는 메서드
     * - 범위: 100,000 ~ 999,999
     *
     * @return 6자리 난수
     */
    public String generate() {
        return String.valueOf(secureRandom.nextInt(900000) + 100000);
    }
}
