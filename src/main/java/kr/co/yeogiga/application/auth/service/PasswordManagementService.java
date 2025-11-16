package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.util.PasswordCodeGenerator;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.auth.service.PasswordCodeService;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.infrastructure.mail.PasswordResetEmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordManagementService {
    private final UserService userService;
    private final PasswordCodeService passwordCodeService;
    private final PasswordResetEmailSender passwordResetEmailSender;
    
    /**
     * 비밀번호 초기화 요청 메서드
     *
     * <p> 사용자 확인을 위한 확인용 코드 생성 및 저장
     *
     * <p> 해당 사용자에게 비밀번호 초기화 링크 메일 전송
     *
     * @param email     사용자 이메일
     * @param username  사용자 아이디
     * @throws CustomException AuthErrorType.MISMATCHED_EMAIL_OR_USERNAME - 이메일 또는 아이디가 불일치하는 경우
     */
    public void requestPasswordReset(String email, String username) {
        if (!userService.existsIncludeDeletedByEmailAndUsername(email, username)) {
            throw new CustomException(AuthErrorType.MISMATCHED_EMAIL_OR_USERNAME);
        }
        
        String code = PasswordCodeGenerator.generate();
        
        passwordCodeService.save(email, code);
        passwordResetEmailSender.send(email, code);
    }
}
