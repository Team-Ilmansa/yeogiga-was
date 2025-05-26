package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.user.dto.FcmTokenReq;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserFcmTokenServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserFcmTokenService userFcmTokenService;

    @Nested
    @DisplayName("토큰 등록 테스트")
    class RegisterFcmToken {

        private final Long userId = 1L;
        private final String fcmToken = "fcm-token";
        private final User user = User.builder()
                .username("test")
                .password("bcryptPassword")
                .email("test@test.com")
                .role(Role.USER)
                .nickname("test")
                .build();

        @Test
        @DisplayName("성공")
        void success() {
            // given
            FcmTokenReq request = new FcmTokenReq(fcmToken);
            given(userService.readById(userId)).willReturn(Optional.ofNullable(user));

            // when
            userFcmTokenService.registerFcmToken(userId, request);

            // then
            assertEquals(fcmToken, user.getFcmToken());
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        void failUserNotFound() {
            // given
            FcmTokenReq request = new FcmTokenReq(fcmToken);
            when(userService.readById(userId)).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    userFcmTokenService.registerFcmToken(userId, request)
            );

            // then
            assertEquals(exception.getErrorType(), UserErrorType.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("토큰 삭제 테스트")
    class DeleteFcmToken {

        private final Long userId = 1L;
        private final User user = User.builder()
                .username("test")
                .password("bcryptPassword")
                .email("test@test.com")
                .role(Role.USER)
                .nickname("test")
                .build();

        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(userService.readById(userId)).willReturn(Optional.ofNullable(user));

            // when
            userFcmTokenService.deleteFcmToken(userId);

            // then
            assertNull(user.getFcmToken());
        }

        @Test
        @DisplayName("실패 - 사용자 존재하지 않음")
        void failUserNotFound() {
            // given
            when(userService.readById(userId)).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    userFcmTokenService.deleteFcmToken(userId)
            );

            // then
            assertEquals(exception.getErrorType(), UserErrorType.NOT_FOUND);
        }
    }
}
