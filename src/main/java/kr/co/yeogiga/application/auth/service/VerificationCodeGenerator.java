package kr.co.yeogiga.application.auth.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {
    private final SecureRandom secureRandom = new SecureRandom();
    
    public String generate() {
        return String.valueOf(secureRandom.nextInt(900000) + 100000);
    }
}
