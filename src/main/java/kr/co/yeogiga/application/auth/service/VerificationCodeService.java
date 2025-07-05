package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
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
    
    public void verifyCode(String email, String code) {
        String verifiedCode = verificationCodeCache.getCode(email)
                .orElseThrow(() -> new CustomException(AuthErrorType.EMAIL_VERIFICATION_TIMEOUT));
        
        if (!verifiedCode.equals(code)) {
            throw new CustomException(AuthErrorType.EMAIL_VERIFICATION_CODE_MISMATCH);
        }
        
        verificationCodeCache.delete(email);
    }
}
