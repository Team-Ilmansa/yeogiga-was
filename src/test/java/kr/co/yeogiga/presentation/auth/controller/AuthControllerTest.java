package kr.co.yeogiga.presentation.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import kr.co.yeogiga.application.auth.constant.AuthConstants;
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.service.AuthService;
import kr.co.yeogiga.application.auth.type.Device;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Cookie refreshTokenInCookie = new Cookie("refreshToken", "test-refresh-token");
    private final String refreshTokenInHeader = "test-refresh-token";

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
    }

    @Nested
    @DisplayName("로그아웃")
    class SignOut {

        @Test
        @DisplayName("성공 - 모바일")
        void successSignOutInMobile() throws Exception {
            // given
            doNothing().when(authService).signOut(refreshTokenInHeader);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/sign-out")
                            .header("refreshToken", refreshTokenInHeader)
                            .header("device", Device.MOBILE)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("성공 - 웹")
        void successSignOutInWeb() throws Exception {
            // given
            doNothing().when(authService).signOut(refreshTokenInCookie.getValue());

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/sign-out")
                            .cookie(refreshTokenInCookie)
                            .header("device", Device.WEB)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class TokenReissue {
        private final TokenDto tokenDto = TokenDto.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

        @Test
        @DisplayName("성공 - 모바일")
        void successReissueTokenInMobile() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenInCookie.getValue())).thenReturn(tokenDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/reissue")
                        .header("device", Device.MOBILE.toString())
                        .header("refreshToken", refreshTokenInHeader)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());

        }

        @Test
        @DisplayName("성공 - 웹")
        void successReissueTokenInWeb() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenInCookie.getValue())).thenReturn(tokenDto);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/reissue")
                        .header("device", Device.WEB.toString())
                        .cookie(refreshTokenInCookie)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(cookie().exists(AuthConstants.REFRESH_TOKEN_PREFIX));

        }

        @Test
        @DisplayName("실패 - 웹 리프레시 토큰 미포함")
        void failReissueTokenInWeb() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenInCookie.getValue())).thenReturn(tokenDto);

            // when

            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/reissue")
                            .header("device", Device.WEB)
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("A009"));

        }

        @Test
        @DisplayName("실패 - 모바일 리프레시 토큰 미포함")
        void failReissueTokenInMobile() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenInHeader)).thenReturn(tokenDto);

            // when

            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/reissue")
                            .header("device", Device.WEB)
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("A009"));

        }

        @Test
        @DisplayName("실패 - 웹 리프레시 토큰 만료")
        void failReissueTokenExpiredTokenInWeb() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenInCookie.getValue())).thenThrow(new CustomException(AuthErrorType.REFRESH_TOKEN_EXPIRED));

            // when

            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/reissue")
                        .header("device", Device.WEB)
                        .cookie(refreshTokenInCookie)
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("A008"));

        }

        @Test
        @DisplayName("실패 - 모바일 리프레시 토큰 만료")
        void failReissueTokenExpiredTokenInMobile() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenInHeader)).thenThrow(new CustomException(AuthErrorType.REFRESH_TOKEN_EXPIRED));

            // when

            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/reissue")
                            .header("device", Device.MOBILE)
                            .header("refreshToken", refreshTokenInHeader)
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("A008"));

        }
    }

    @Nested
    @DisplayName("회원가입")
    class SignUp {

        @Test
        @DisplayName("성공")
        void successSignUp() throws Exception {
            // given
            doNothing().when(authService).signUp(any());
            SignUpDto.Request signUpDto = SignUpDto.Request.builder()
                    .username("testid")
                    .email("test@test.com")
                    .password("testpw")
                    .nickname("testnick")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/sign-up")
                            .content(objectMapper.writeValueAsBytes(signUpDto))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(SuccessResponse.created().code()))
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증")
        void failSignUpValidation() throws Exception {
            // given
            doNothing().when(authService).signUp(any());
            SignUpDto.Request signUpDto = SignUpDto.Request.builder()
//                    .username("testid")   // username 누락
                    .email("test@testcom")  // email 누락
                    .password("testpw")
                    .nickname("testnick")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/sign-up")
                            .content(objectMapper.writeValueAsBytes(signUpDto))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(CommonErrorType.VALIDATION_ERROR.getCode()))
                    .andExpect(jsonPath("$.errors").exists());
        }

        @Test
        @DisplayName("실패 - 이미 존재하는 아이디")
        void failSignUpAlreadyExists() throws Exception {
            // given
            doThrow(new CustomException(UserErrorType.ALREADY_EXIST_USERNAME)).when(authService).signUp(any());
            SignUpDto.Request signUpDto = SignUpDto.Request.builder()
                    .username("testid")
                    .email("test@test.com")
                    .password("testpw")
                    .nickname("testnick")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/sign-up")
                            .content(objectMapper.writeValueAsBytes(signUpDto))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(UserErrorType.ALREADY_EXIST_USERNAME.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.ALREADY_EXIST_USERNAME.getMessage()));
        }
    }

    @Nested
    @DisplayName("일반 로그인")
    class SignIn {
        private TokenDto token = TokenDto.builder()
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

        @Test
        @DisplayName("성공 - 웹")
        void successSignInInWeb() throws Exception {
            // given
            SignInDto.Request request = SignInDto.Request.builder()
                    .username("testid")
                    .password("testpw")
                    .build();

            when(authService.signIn(request)).thenReturn(token);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/sign-in")
                            .header("device", Device.WEB)
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").value(token.accessToken()))
                    .andExpect(cookie().value("refreshToken", token.refreshToken()));
        }

        @Test
        @DisplayName("성공 - 모바일")
        void successSignInInMobile() throws Exception {
            // given
            SignInDto.Request request = SignInDto.Request.builder()
                    .username("testid")
                    .password("testpw")
                    .build();

            when(authService.signIn(request)).thenReturn(token);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/sign-in")
                            .header("device", Device.MOBILE)
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").value(token.accessToken()))
                    .andExpect(jsonPath("$.data.refreshToken").value(token.refreshToken()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증 실패")
        void failSignInValidation() throws Exception {
            // given
            SignInDto.Request request = SignInDto.Request.builder()
//                    .username("testid")   // username 누락
                    .password("testpw")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/sign-in")
                            .header("device", Device.WEB)
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 아이디 또는 비밀번호")
        void failSignInNotExists() throws Exception {
            // given
            SignInDto.Request request = SignInDto.Request.builder()
                    .username("testid")
                    .password("testpw")
                    .build();

            doThrow(new CustomException(AuthErrorType.AUTHENTICATION_FAIL)).when(authService).signIn(request);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/sign-in")
                            .header("device", Device.WEB)
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(AuthErrorType.AUTHENTICATION_FAIL.getCode()))
                    .andExpect(jsonPath("$.message").value(AuthErrorType.AUTHENTICATION_FAIL.getMessage()));;
        }
    }
}
