package kr.co.yeogiga.presentation.auth.controller;

import jakarta.servlet.http.Cookie;
import kr.co.yeogiga.application.auth.constant.AuthConstants;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.service.AuthService;
import kr.co.yeogiga.application.auth.type.Device;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private final Cookie refreshTokenCookie = new Cookie("refreshToken", "test-refresh-token");

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("로그아웃")
    void successSignOut() throws Exception {
        // given
        doNothing().when(authService).signOut(refreshTokenCookie.toString());

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auth/sign-out")
                .cookie(refreshTokenCookie)
        );

        // then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
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
            when(authService.reissueToken(refreshTokenCookie.getValue())).thenReturn(tokenDto);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/auth/reissue")
                    .header("device", Device.MOBILE.toString())
                    .cookie(refreshTokenCookie)
            );

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());

        }

        @Test
        @DisplayName("성공 - 웹")
        void successReissueTokenInWeb() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenCookie.getValue())).thenReturn(tokenDto);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/auth/reissue")
                    .header("device", Device.WEB.toString())
                    .cookie(refreshTokenCookie)
            );

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(cookie().exists(AuthConstants.REFRESH_TOKEN_PREFIX));

        }

        @Test
        @DisplayName("실패 - 쿠키 미포함")
        void failReissueToken() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenCookie.getValue())).thenReturn(tokenDto);

            // when

            ResultActions result = mockMvc.perform(get("/api/v1/auth/reissue")
                    .header("device", Device.WEB)
            );

            // then
            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("A009"));

        }

        @Test
        @DisplayName("실패 - 리프레시 토큰 만료")
        void failReissueTokenExpiredToken() throws Exception {
            // given
            when(authService.reissueToken(refreshTokenCookie.getValue())).thenThrow(new CustomException(AuthErrorType.REFRESH_TOKEN_EXPIRED));

            // when

            ResultActions result = mockMvc.perform(get("/api/v1/auth/reissue")
                    .header("device", Device.WEB)
                    .cookie(refreshTokenCookie)
            );

            // then
            result
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("A008"));

        }
    }
}
