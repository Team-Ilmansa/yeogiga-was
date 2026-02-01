package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.event.EmailVerificationEvent;
import kr.co.yeogiga.application.event.publisher.DomainEventPublisher;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.util.VerificationCodeGenerator;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.auth.repository.VerificationCodeRepository;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserService userService;
    private final DomainEventPublisher eventPublisher;
    
    private final Long VERIFICATION_CODE_SEND_TIME_LIMIT = 2 * 60L;
    
    @Value("${auth.expiration.email-verification}")
    private int emailVerificationExpiration;
    
    /**
     * 사용자가 인증 요청한 이메일로 인증 번호를 발송하는 메서드
     *
     * @throws CustomException AuthErrorType.ALREADY_USED_EMAIL - 이미 사용 중인 이메일일 경우
     * @throws CustomException AuthErrorType.EMAIL_VERIFICATION_TIME_LIMIT - 이메일 인증 시도 횟수 초과 (1분 이내 재요청)
     *
     * @param email 인증 요청 이메일
     */
    @Transactional
    public void issueCode(String email) {
        if (userService.existsIncludeDeletedByEmail(email)) {
            throw new CustomException(AuthErrorType.ALREADY_USED_EMAIL);
        }
        
        String code = VerificationCodeGenerator.generate();
        
        Long expiration = verificationCodeRepository.getExpire(email);
        if (expiration > VERIFICATION_CODE_SEND_TIME_LIMIT) {
            throw new CustomException(AuthErrorType.EMAIL_VERIFICATION_TIME_LIMIT);
        }
        
        verificationCodeRepository.save(email, code, emailVerificationExpiration);
        
        eventPublisher.publish(new EmailVerificationEvent(email, code, emailVerificationExpiration));
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
