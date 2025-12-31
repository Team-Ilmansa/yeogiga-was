package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.event.PasswordResetEvent;
import kr.co.yeogiga.application.event.publisher.DomainEventPublisher;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.auth.service.PasswordCodeService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    public DomainEventPublisher eventPublisher;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private PasswordManagementService passwordManagementService;
    
    @Nested
    @DisplayName("비밀번호 초기화 요청")
    class RequestPasswordReset {
        private final String email = "test@test.com";
        private final String username = "test";
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(userService.existsIncludeDeletedByEmailAndUsername(email, username)).thenReturn(true);
            when(passwordCodeService.existsCode(email)).thenReturn(false);
            doNothing().when(passwordCodeService).save(eq(email), anyString());
            doNothing().when(eventPublisher).publish(any(PasswordResetEvent.class));
            
            // when
            passwordManagementService.requestPasswordReset(email, username);
            
            // then
            verify(passwordCodeService, times(1)).save(eq(email), anyString());
            verify(eventPublisher, times(1)).publish(any(PasswordResetEvent.class));
        }
        
        @Test
        @DisplayName("실패 - 최초 요청 후 시간 내 재요청한 경우")
        void failBecausePasswordResetTimeLimit() {
            // given
            when(userService.existsIncludeDeletedByEmailAndUsername(email, username)).thenReturn(true);
            when(passwordCodeService.existsCode(email)).thenReturn(true);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> passwordManagementService.requestPasswordReset(email, username));
            
            // then
            assertEquals(AuthErrorType.PASSWORD_RESET_TIME_LIMIT, exception.getErrorType());
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
    
    @Nested
    @DisplayName("비밀번호 초기화")
    class ResetPassword {
        private final String email = "test@test.com";
        private final String username = "username";
        private final String code = UUID.randomUUID().toString();
        private final String newPassword = "newPassword";
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            User user = User.builder().build();
            String encryptedPassword = "encrypted-newPassword";
            when(userService.readIncludeDeletedUserByEmailAndUsername(email, username)).thenReturn(Optional.of(user));
            when(passwordCodeService.getCode(email)).thenReturn(code);
            when(passwordEncoder.encode(newPassword)).thenReturn(encryptedPassword);
            doNothing().when(passwordCodeService).del(email);
            
            // then
            passwordManagementService.resetPassword(email, username, code, newPassword);
            
            // then
            assertEquals(encryptedPassword, user.getPassword());
        }
        
        @Test
        @DisplayName("실패 - 이메일 또는 아이디 불일치")
        void failIfEmailAndUsernameMismatch() {
            // given
            when(userService.readIncludeDeletedUserByEmailAndUsername(email, username)).thenReturn(Optional.empty());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> passwordManagementService.resetPassword(email, username, code, newPassword));
            
            // then
            assertEquals(AuthErrorType.MISMATCHED_EMAIL_OR_USERNAME, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 비밀번호 초기화 인증 시간 초과")
        void failIfPasswordResetTimeout() {
            // given
            User user = User.builder().build();
            when(userService.readIncludeDeletedUserByEmailAndUsername(email, username)).thenReturn(Optional.of(user));
            when(passwordCodeService.getCode(email)).thenReturn(null);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> passwordManagementService.resetPassword(email, username, code, newPassword));
            
            // then
            assertEquals(AuthErrorType.PASSWORD_RESET_TIMEOUT, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 비밀번호 초기화 확인용 코드 불일치")
        void failIfPasswordCodeMismatch() {
            // given
            User user = User.builder().build();
            when(userService.readIncludeDeletedUserByEmailAndUsername(email, username)).thenReturn(Optional.of(user));
            when(passwordCodeService.getCode(email)).thenReturn("mismatch-password");
            
            // then
            CustomException exception = assertThrows(CustomException.class, ()
                    -> passwordManagementService.resetPassword(email, username, code, newPassword));
            
            // then
            assertEquals(AuthErrorType.PASSWORD_CODE_MISMATCH, exception.getErrorType());
        }
    }
}
