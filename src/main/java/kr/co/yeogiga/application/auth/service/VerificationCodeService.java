package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.domain.auth.repository.VerificationCodeCache;
import kr.co.yeogiga.infrastructure.mail.VerificationCodeEmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationCodeService {
    private final VerificationCodeCache verificationCodeCache;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final VerificationCodeEmailSender verificationCodeEmailSender;

    public void issueCode(String email) {
        String code = verificationCodeGenerator.generate();
        
        verificationCodeCache.save(email, code);
        
        verificationCodeEmailSender.send(email, code);
    }
}
