package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
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

    @Mock
    private PasswordEncoder passwordEncoder;

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

        // when
        TokenDto reissuedToken = authService.reissueToken("old-refresh-token");

        // then
        assertEquals(tokenDto, reissuedToken);
    }

    @Test
    @DisplayName("토큰 재발급 - 실패: 리프레시 토큰 만료")
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

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUp {

        @Captor
        private ArgumentCaptor<String> stringCaptor;

        @Captor
        private ArgumentCaptor<User> userCaptor;

        @Test
        @DisplayName("성공")
        void success() {
            // given
            SignUpDto.Request request = SignUpDto.Request.builder()
                    .username("testid")
                    .email("test@test.com")
                    .nickname("testnick")
                    .password("testpw")
                    .build();

            when(userService.existsIncludeDeletedByUsername(request.username())).thenReturn(false);
            when(userService.existsIncludeDeletedByNickname(request.nickname())).thenReturn(false);
            when(userService.existsIncludeDeletedByEmail(request.email())).thenReturn(false);
            when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
            doNothing().when(userService).save(any());

            // when
            authService.signUp(request);

            // then
            verify(passwordEncoder, times(1)).encode(stringCaptor.capture());
            verify(userService, times(1)).save(userCaptor.capture());

            assertEquals("testpw", stringCaptor.getValue());
            assertEquals("encodedPassword", userCaptor.getValue().getPassword());
        }

        @Test
        @DisplayName("실패 - 이미 사용 중인 아이디")
        void failAlreadyUsedUsername() {
            // given
            SignUpDto.Request request = SignUpDto.Request.builder()
                    .username("testid")
                    .email("test@test.com")
                    .nickname("testnick")
                    .password("testpw")
                    .build();

            when(userService.existsIncludeDeletedByUsername(request.username())).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> authService.signUp(request));

            // then
            assertEquals(exception.getErrorType(), AuthErrorType.ALREADY_USED_USERNAME);
        }

        @Test
        @DisplayName("실패 - 이미 사용 중인 닉네임")
        void failAlreadyUsedNickname() {
            // given
            SignUpDto.Request request = SignUpDto.Request.builder()
                    .username("testid")
                    .email("test@test.com")
                    .nickname("testnick")
                    .password("testpw")
                    .build();

            when(userService.existsIncludeDeletedByUsername(request.username())).thenReturn(false);
            when(userService.existsIncludeDeletedByNickname(request.nickname())).thenReturn(true);


            // when
            CustomException exception = assertThrows(CustomException.class, () -> authService.signUp(request));

            // then
            assertEquals(exception.getErrorType(), AuthErrorType.ALREADY_USED_NICKNAME);
        }

        @Test
        @DisplayName("실패 - 이미 사용 중인 이메일")
        void failAlreadyUsedEmail() {
            // given
            SignUpDto.Request request = SignUpDto.Request.builder()
                    .username("testid")
                    .email("test@test.com")
                    .nickname("testnick")
                    .password("testpw")
                    .build();

            when(userService.existsIncludeDeletedByUsername(request.username())).thenReturn(false);
            when(userService.existsIncludeDeletedByNickname(request.nickname())).thenReturn(false);
            when(userService.existsIncludeDeletedByEmail(request.email())).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> authService.signUp(request));

            // then
            assertEquals(exception.getErrorType(), AuthErrorType.ALREADY_USED_EMAIL);
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class SignIn {
        private SignInDto.Request request = SignInDto.Request.builder()
                .username("testid")
                .password("testpw")
                .build();

        @Test
        @DisplayName("성공")
        void successSignInInWeb() {
            // given
            User newUser = User.builder()
                    .username("testid")
                    .email("test@test.com")
                    .nickname("testnick")
                    .password("testpw")
                    .build();

            when(userService.readIncludeDeletedUserByUsername(request.username())).thenReturn(Optional.of(newUser));
            when(passwordEncoder.matches(request.password(), newUser.getPassword())).thenReturn(true);
            when(jwtService.generateToken(any(), any(), any())).thenReturn(tokenDto);

            // when
            TokenDto token = authService.signIn(request);

            // then
            assertNotNull(token);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 아이디")
        void failSignInNotFountId() {
            // given
            when(userService.readIncludeDeletedUserByUsername(request.username())).thenReturn(Optional.ofNullable(null));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> authService.signIn(request));

            // then
            assertEquals(exception.getErrorType(), AuthErrorType.AUTHENTICATION_FAIL);
        }

        @Test
        @DisplayName("실패 - 패스워드 불일치")
        void failSignInPasswordMisMatch() {
            // given
            User newUser = User.builder()
                    .username("testid")
                    .email("test@test.com")
                    .nickname("testnick")
                    .password("testpw")
                    .build();

            when(userService.readIncludeDeletedUserByUsername(request.username())).thenReturn(Optional.of(newUser));
            when(passwordEncoder.matches(request.password(), newUser.getPassword())).thenReturn(false);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> authService.signIn(request));

            // then
            assertEquals(exception.getErrorType(), AuthErrorType.AUTHENTICATION_FAIL);
        }
        
        @Test
        @DisplayName("실패 - 이미 탈퇴(소프트 딜리트)한 사용자")
        void failWhenDeletedUserSignIn() {
            // given
            User user = User.builder()
                    .username("testid")
                    .email("test@test.com")
                    .nickname("testnick")
                    .password("testpw")
                    .build();
            
            ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now());
            
            when(userService.readIncludeDeletedUserByUsername(request.username())).thenReturn(Optional.of(user));
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> authService.signIn(request));
            
            // then
            assertEquals(UserErrorType.ALREADY_WITHDRAW, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("아이디 중복 확인")
    class DupCheckUsername {
        @Test
        @DisplayName("사용 가능한 아이디")
        void success() {
            // given
            String username = "test";
            when(userService.existsIncludeDeletedByUsername(username)).thenReturn(false);

            // when & then
            assertDoesNotThrow(() -> authService.checkDuplicatedUsername(username));
        }

        @Test
        @DisplayName("이미 사용 중인 아이디")
        void failAlreadyUsedUsername() {
            // given
            String username = "test";
            when(userService.existsIncludeDeletedByUsername(username)).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> authService.checkDuplicatedUsername(username));

            // then
            assertEquals(AuthErrorType.ALREADY_USED_USERNAME, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("닉네임 중복 확인")
    class DupCheckNickname {
        @Test
        @DisplayName("사용 가능한 닉네임")
        void success() {
            // given
            String nickname = "test";
            when(userService.existsIncludeDeletedByNickname(nickname)).thenReturn(false);

            // when & then
            assertDoesNotThrow(() -> authService.checkDuplicatedNickname(nickname));
        }

        @Test
        @DisplayName("이미 사용 중인 닉네임")
        void failAlreadyUsedNickname() {
            // given
            String nickname = "test";
            when(userService.existsIncludeDeletedByNickname(nickname)).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> authService.checkDuplicatedNickname(nickname));

            // then
            assertEquals(AuthErrorType.ALREADY_USED_NICKNAME, exception.getErrorType());
        }
    }
}
