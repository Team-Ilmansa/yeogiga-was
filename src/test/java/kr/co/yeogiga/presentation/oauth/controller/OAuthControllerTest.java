package kr.co.yeogiga.presentation.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.service.OAuthManagementService;
import kr.co.yeogiga.application.auth.type.Device;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.presentation.auth.controller.OAuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthController.class)
public class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OAuthManagementService oAuthManagementService;

    private Device device;
    private OAuthPlatform oAuthPlatform;
    private SignInDto.OAuthRequest request;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();

        request = SignInDto.OAuthRequest.builder()
                .code("testCode")
                .build();
    }

    void setSignInMethod(boolean shouldSignup) {
        when(oAuthManagementService.signIn(any(), any())).thenReturn(
                SignInDto.Response.builder()
                        .token(TokenDto.builder()
                                .accessToken("test_access_token")
                                .refreshToken("test_refresh_token")
                                .build())
                        .shouldSignup(shouldSignup)
                        .build()
        );
    }

    @Test
    @DisplayName("처음 로그인한 사용자 (웹)")
    void signInFirstFromWeb() throws Exception {
        // given
        device = Device.WEB;
        oAuthPlatform = OAuthPlatform.NAVER;
        setSignInMethod(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/oauth/sign-in/{platform}", oAuthPlatform)
                        .header("device", device.name())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.data.token.accessToken").exists())
                .andExpect(jsonPath("$.data.shouldSignup").value(true));
    }

    @Test
    @DisplayName("처음 로그인한 사용자 (모바일)")
    void signInFirstFormMobile() throws Exception {
        // given
        device = Device.MOBILE;
        oAuthPlatform = OAuthPlatform.NAVER;
        setSignInMethod(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/oauth/sign-in/{platform}", oAuthPlatform)
                        .header("device", device.name())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"))
                .andExpect(jsonPath("$.data.token.accessToken").exists())
                .andExpect(jsonPath("$.data.token.refreshToken").exists())
                .andExpect(jsonPath("$.data.shouldSignup").value(true));
    }

    @Test
    @DisplayName("이미 회원가입한 사용자 (웹)")
    void signInFromWeb() throws Exception {
        // given
        device = Device.WEB;
        oAuthPlatform = OAuthPlatform.NAVER;
        setSignInMethod(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/oauth/sign-in/{platform}", oAuthPlatform)
                        .header("device", device.name())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.data.token.accessToken").exists())
                .andExpect(jsonPath("$.data.shouldSignup").value(false));
    }

    @Test
    @DisplayName("이미 회원가입한 사용자 (모바일)")
    void signInFormMobile() throws Exception {
        // given
        device = Device.MOBILE;
        oAuthPlatform = OAuthPlatform.NAVER;
        setSignInMethod(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/oauth/sign-in/{platform}", oAuthPlatform)
                        .header("device", device.name())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("refreshToken"))
                .andExpect(jsonPath("$.data.token.accessToken").exists())
                .andExpect(jsonPath("$.data.token.refreshToken").exists())
                .andExpect(jsonPath("$.data.shouldSignup").value(false));
    }

    @Test
    @DisplayName("OAuth 인증 코드 누락")
    void missOAuthCode() throws Exception {
        // given
        device = Device.MOBILE;
        oAuthPlatform = OAuthPlatform.NAVER;
        SignInDto.OAuthRequest req = SignInDto.OAuthRequest.builder()
                        .build();


        setSignInMethod(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/oauth/sign-in/{platform}", oAuthPlatform)
                        .header("device", device.name())
                        .content(objectMapper.writeValueAsBytes(req))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value("G002"));
    }

    @Test
    @DisplayName("OAuth 인증 코드 공백값")
    void emptyOAuthCode() throws Exception {
        // given
        device = Device.MOBILE;
        oAuthPlatform = OAuthPlatform.NAVER;
        SignInDto.OAuthRequest req = SignInDto.OAuthRequest.builder()
                .code("   ")
                .build();


        setSignInMethod(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/oauth/sign-in/{platform}", oAuthPlatform)
                        .header("device", device.name())
                        .content(objectMapper.writeValueAsBytes(req))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value("G002"));
    }

    @Test
    @DisplayName("지원하지 않는 플랫폼")
    void unsupportedPlatform() throws Exception {
        // given
        device = Device.MOBILE;
        setSignInMethod(false);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/oauth/sign-in/{platform}", "MISS_VALUE")
                        .header("device", device.name())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.code").value("G003"));
    }
}