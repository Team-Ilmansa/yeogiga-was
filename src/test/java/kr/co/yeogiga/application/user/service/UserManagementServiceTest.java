package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.auth.service.RefreshTokenService;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.application.user.dto.UserInfoRes;
import kr.co.yeogiga.application.user.dto.UserInfoUpdateReq;
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

import static org.assertj.core.api.Assertions.assertThat;
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

    @Nested
    @DisplayName("회원 정보 조회")
    class GetUserInfo {
        private final Long userId = 1L;
        @Test
        @DisplayName("성공 - 소셜 로그인 사용자")
        void successForSocialUser() {
            // given
            User socialUser = User.builder()
                    .username("KAKAO 123")
                    .nickname("nickname")
                    .role(Role.USER)
                    .email("test@test.com")
                    .build();
            when(userService.readById(eq(userId))).thenReturn(Optional.of(socialUser));

            // when
            UserInfoRes userInfoRes = userManagementService.getUserInfo(userId);

            // then
            assertEquals("nickname", userInfoRes.nickname());
            assertEquals("test@test.com", userInfoRes.email());
            assertThat(userInfoRes.username()).isNull();
        }

        @Test
        @DisplayName("성공 - 일반 로그인 사용자")
        void successForNormalUser() {
            // given
            User socialUser = User.builder()
                    .username("username")
                    .password("password")
                    .nickname("nickname")
                    .role(Role.USER)
                    .email("test@test.com")
                    .build();
            when(userService.readById(eq(userId))).thenReturn(Optional.of(socialUser));

            // when
            UserInfoRes userInfoRes = userManagementService.getUserInfo(userId);

            // then
            assertEquals("nickname", userInfoRes.nickname());
            assertEquals("username", userInfoRes.username());
            assertEquals("test@test.com", userInfoRes.email());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void failUserNotFound() {
            // given
            when(userService.readById(eq(userId))).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, () -> userManagementService.getUserInfo(userId));

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("회원 정보 수정")
    class UpdateUserInfo {
        private final Long userId = 1L;

        private User user = User.builder()
            .id(userId)
            .nickname("nickname")
            .build();

        private UserInfoUpdateReq userInfoUpdateReq = UserInfoUpdateReq.builder()
                .nickname("newNickname")
                .build();

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(userService.readById(eq(userId))).thenReturn(Optional.of(user));
            when(userService.existsIncludeDeletedByNickname(eq("newNickname"))).thenReturn(false);

            // when
            userManagementService.updateUserInfo(userId, userInfoUpdateReq);

            // then
            assertEquals(userInfoUpdateReq.nickname(), user.getNickname());

        }

        @Test
        @DisplayName("실패 - 이미 사용 중인 닉네임")
        void failAlreadyUsedNickname() {
            // given
            when(userService.readById(userId)).thenReturn(Optional.of(user));
            when(userService.existsIncludeDeletedByNickname("newNickname")).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> userManagementService.updateUserInfo(userId, userInfoUpdateReq));

            // then
            assertEquals(UserErrorType.ALREADY_USED_NICKNAME, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 기존과 동일한 닉네임")
        void failSameNickname() {
            // given
            UserInfoUpdateReq sameUserInfoUpdateReq = UserInfoUpdateReq.builder()
                    .nickname("nickname")
                    .build();
            
            when(userService.readById(eq(userId))).thenReturn(Optional.of(user));

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> userManagementService.updateUserInfo(userId, sameUserInfoUpdateReq));

            // then
            assertEquals(UserErrorType.SAME_NICKNAME, exception.getErrorType());
        }
    }
}
