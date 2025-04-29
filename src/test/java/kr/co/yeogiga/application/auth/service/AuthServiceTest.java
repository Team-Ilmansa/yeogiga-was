package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private final Long userId = 1L;

    private final User user = User.builder()
            .nickname("test")
            .username("test")
            .password("test")
            .email("test@test.com")
            .role(Role.USER)
            .build();

    private final TokenDto tokenDto = TokenDto.builder()
            .accessToken("test-access-token")
            .refreshToken("test-refresh-token")
            .build();

    @Test
    @DisplayName("토큰 재발급 - 성공")
    void successReissueToken() {
        // given
        when(jwtService.extractUserId(any())).thenReturn(userId);
        when(refreshTokenService.exists(userId)).thenReturn(true);
        when(userService.readById(userId)).thenReturn(Optional.ofNullable(user));
        when(jwtService.generateToken(any(), any(), any())).thenReturn(tokenDto);
        doNothing().when(refreshTokenService).save(userId, tokenDto.refreshToken());

        // when
        TokenDto reissuedToken = authService.reissueToken("old-refresh-token");

        // then
        verify(refreshTokenService).save(userId, reissuedToken.refreshToken());
    }

    @Test
    @DisplayName("토큰 재발급 - 실패")
    void failReissueToken() {
        // given
        when(jwtService.extractUserId(any())).thenReturn(userId);
        when(refreshTokenService.exists(userId)).thenReturn(false); // refresh token 만료

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                authService.reissueToken("old-refresh-token")
        );

        // then
        assertEquals(AuthErrorType.REFRESH_TOKEN_EXPIRED, exception.getErrorType());
    }

}
