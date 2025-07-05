package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.auth.repository.VerificationCodeCache;
import kr.co.yeogiga.infrastructure.mail.VerificationCodeEmailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerificationCodeServiceTest {
    @Mock
    private VerificationCodeCache verificationCodeCache;
    
    @Mock
    private VerificationCodeGenerator verificationCodeGenerator;
    
    @Mock
    private VerificationCodeEmailSender verificationCodeEmailSender;
    
    @InjectMocks
    private VerificationCodeService verificationCodeService;
    
    private final String email = "test@test.com";
    private final String code = "123456";
    
    @Nested
    @DisplayName("이메일 인증 코드 발급 요청")
    class IssueCode {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            
            when(verificationCodeGenerator.generate()).thenReturn(code);
            doNothing().when(verificationCodeCache).save(anyString(), anyString());
            doNothing().when(verificationCodeEmailSender).send(anyString(), anyString());
            
            // when
            verificationCodeService.issueCode(email);
            
            // then
            verify(verificationCodeCache, times(1)).save(eq(email), eq(code));
            verify(verificationCodeEmailSender, times(1)).send(eq(email), eq(code));
        }
    }
    
    @Nested
    @DisplayName("이메일 인증 코드 검증")
    class VerifyCode {
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(verificationCodeCache.getCode(anyString())).thenReturn(Optional.of(code));
            doNothing().when(verificationCodeCache).delete(anyString());
            
            // when
            verificationCodeService.verifyCode(email, code);
            
            // then
            verify(verificationCodeCache, times(1)).delete(eq(email));
        }
        
        @Test
        @DisplayName("실패 - 인증 코드 저장소 내 코드 미존재")
        void failIfCodeNotStoredInRepository() {
            // given
            when(verificationCodeCache.getCode(anyString())).thenReturn(Optional.empty());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> verificationCodeService.verifyCode(email, code));
            
            // then
            assertEquals(AuthErrorType.EMAIL_VERIFICATION_TIMEOUT, exception.getErrorType());
            verify(verificationCodeCache, never()).delete(anyString());
        }
        
        @Test
        @DisplayName("실패 - 인증 코드 불일치")
        void failIfCodeMisMatched() {
            // given
            when(verificationCodeCache.getCode(anyString())).thenReturn(Optional.of("999999"));
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> verificationCodeService.verifyCode(email, code));
            
            // then
            assertEquals(AuthErrorType.EMAIL_VERIFICATION_CODE_MISMATCH, exception.getErrorType());
            verify(verificationCodeCache, never()).delete(anyString());
        }
    }
}
