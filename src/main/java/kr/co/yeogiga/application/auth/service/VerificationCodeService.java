package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.auth.repository.VerificationCodeRepository;
import kr.co.yeogiga.infrastructure.mail.VerificationCodeEmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final VerificationCodeEmailSender verificationCodeEmailSender;
    
    /**
     * 사용자가 인증 요청한 이메일로 인증 번호를 발송하는 메서드
     *
     * @param email 인증 요청 이메일
     */
    public void issueCode(String email) {
        String code = verificationCodeGenerator.generate();
        
        verificationCodeRepository.save(email, code);
        
        verificationCodeEmailSender.send(email, code);
    }
    
    /**
     * 사용자가 요청한 이메일 인증 번호를 검증하는 메서드
     *
     * @param email 사용자 인증 요청 이메일
     * @param code  사용자가 요청한 이메일 인증 번호
     * @throws CustomException AuthErrorType.EMAIL_VERIFICATION_TIMEOUT - 저장소 내 이메일 존재하지 않거나 인증 시간이 초과된 경우
     * @throws CustomException AuthErrorType.EMAIL_VERIFICATION_CODE_MISMATCH - 이메일 인증 번호 불일치
     */
    public void verifyCode(String email, String code) {
        String verifiedCode = verificationCodeRepository.getCode(email)
                .orElseThrow(() -> new CustomException(AuthErrorType.EMAIL_VERIFICATION_TIMEOUT));
        
        if (!verifiedCode.equals(code)) {
            throw new CustomException(AuthErrorType.EMAIL_VERIFICATION_CODE_MISMATCH);
        }
        
        verificationCodeRepository.delete(email);
    }
}
