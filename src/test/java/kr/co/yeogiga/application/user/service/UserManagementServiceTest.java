package kr.co.yeogiga.application.user.service;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

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
            when(passwordEncoder.matches(eq(request.originalPassword()), eq(user.getPassword()))).thenReturn(false);
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
            when(passwordEncoder.matches(eq(request.originalPassword()), eq(user.getPassword()))).thenReturn(false);
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
            when(passwordEncoder.matches(eq(request.originalPassword()), eq(user.getPassword()))).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> userManagementService.updatePassword(userId, request));

            // then
            assertEquals(UserErrorType.PASSWORD_MISMATCH, exception.getErrorType());
        }
    }
}
