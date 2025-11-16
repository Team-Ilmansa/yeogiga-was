package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.auth.service.PasswordCodeService;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.infrastructure.mail.PasswordResetEmailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordManagementServiceTest {
    @Mock
    private UserService userService;
    
    @Mock
    private PasswordCodeService passwordCodeService;
    
    @Mock
    private PasswordResetEmailSender passwordResetEmailSender;
    
    @InjectMocks
    private PasswordManagementService passwordManagementService;
    
    @Nested
    @DisplayName("패스워드 초기화 요청")
    class RequestPasswordReset {
        private final String email = "test@test.com";
        private final String username = "test";
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(userService.existsIncludeDeletedByEmailAndUsername(email, username)).thenReturn(true);
            doNothing().when(passwordCodeService).save(eq(email), anyString());
            doNothing().when(passwordResetEmailSender).send(eq(email), anyString());
            
            // when
            passwordManagementService.requestPasswordReset(email, username);
            
            // then
            verify(passwordCodeService, times(1)).save(eq(email), anyString());
            verify(passwordResetEmailSender, times(1)).send(eq(email), anyString());
        }
        
        @Test
        @DisplayName("실패 - 이메일 또는 아이디가 불일치하는 경우")
        void failIfEmailOrPasswordMismatch() {
            // given
            when(userService.existsIncludeDeletedByEmailAndUsername(email, username)).thenReturn(false);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> passwordManagementService.requestPasswordReset(email, username));
            
            // then
            assertEquals(AuthErrorType.MISMATCHED_EMAIL_OR_USERNAME, exception.getErrorType());
        }
    }
}
