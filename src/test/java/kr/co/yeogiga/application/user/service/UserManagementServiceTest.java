package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.auth.service.RefreshTokenService;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserManagementService userManagementService;

    @Nested
    @DisplayName("비밀번호 갱신")
    class UpdatePassword {
        private final Long userId = 1L;

        private final User user = User.builder()
                .username("test")
                .password("bcryptPassword")
                .email("test@test.com")
                .role(Role.USER)
                .nickname("test")
                .build();

        private final PasswordUpdateReq request = new PasswordUpdateReq("originalPassword", "newPassword");

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(userService.readById(eq(userId))).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(eq(request.originalPassword()), eq(user.getPassword()))).thenReturn(true);
            when(passwordEncoder.matches(eq(request.newPassword()), eq(user.getPassword()))).thenReturn(false);
            when(passwordEncoder.encode(eq(request.newPassword()))).thenReturn("BcryptPassword");

            // when
            userManagementService.updatePassword(userId, request);

            // then
            verify(passwordEncoder, times(1)).encode(eq(request.newPassword()));
        }

        @Test
        @DisplayName("실패 - 기존 사용 비밀번호")
        void failIfPasswordAlreadyUsed() {
            // given
            when(userService.readById(eq(userId))).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(eq(request.originalPassword()), eq(user.getPassword()))).thenReturn(true);
            when(passwordEncoder.matches(eq(request.newPassword()), eq(user.getPassword()))).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> userManagementService.updatePassword(userId, request));

            // then
            assertEquals(UserErrorType.SAME_PASSWORD, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 현재 비밀번호 불일치")
        void failIfOriginalPasswordNotMatch() {
            // given
            when(userService.readById(eq(userId))).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(eq(request.originalPassword()), eq(user.getPassword()))).thenReturn(false);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> userManagementService.updatePassword(userId, request));

            // then
            assertEquals(UserErrorType.PASSWORD_MISMATCH, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class Withdraw {
        private final Long userId = 1L;
        private final User user = User.builder()
                .username("test")
                .nickname("test")
                .password("test")
                .email("test@test.com")
                .role(Role.USER)
                .build();
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(userService.readIncludeDeletedUserById(eq(userId))).thenReturn(Optional.of(user));
            doNothing().when(refreshTokenService).delete(eq(userId));
            doNothing().when(userService).deleteById(eq(userId));

            // when
            userManagementService.withdraw(userId);

            // then
            verify(userService, times(1)).readIncludeDeletedUserById(eq(userId));
            verify(refreshTokenService, times(1)).delete(eq(userId));
            verify(userService, times(1)).deleteById(eq(userId));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void failUserNotFound() {
            // given
            when(userService.readIncludeDeletedUserById(eq(userId))).thenReturn(Optional.empty());
            // when
            CustomException exception = assertThrows(CustomException.class, () -> userManagementService.withdraw(userId));

            // then
            assertEquals(exception.getErrorType(), UserErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("실패 - 이미 회원탈퇴를 진행한 경우")
        void failAlreadyWithdraw() {
            // given
            when(userService.readIncludeDeletedUserById(eq(userId))).thenReturn(Optional.of(user));
            ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now());

            // when
            CustomException exception = assertThrows(CustomException.class, () -> userManagementService.withdraw(userId));

            // then
            assertEquals(exception.getErrorType(), UserErrorType.ALREADY_WITHDRAW);
        }


    }
}
