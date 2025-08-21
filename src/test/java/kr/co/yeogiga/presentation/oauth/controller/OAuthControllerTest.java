package kr.co.yeogiga.presentation.oauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.service.OAuthManagementService;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import kr.co.yeogiga.presentation.auth.controller.OAuthController;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = OAuthController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OAuthManagementService oAuthManagementService;
    
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
    }
    
    private SignInDto.Response getSingInResponse(boolean shouldSignUp) {
        return SignInDto.Response.builder()
                .token(
                        TokenDto.builder()
                                .accessToken("accessToken.xxx.xxx")
                                .refreshToken("refreshToken.xxx.xxx")
                                .build()
                )
                .shouldSignup(shouldSignUp)
                .build();
    }
    
    @Nested
    @DisplayName("소셜 로그인 - 웹")
    class SignInOnWeb {
        private SignInDto.OAuthRequest.Web request = SignInDto.OAuthRequest.Web.builder()
                .code("authorization_code")
                .build();
        
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            when(oAuthManagementService.signInOnWeb(any(OAuthPlatform.class), anyString()))
                    .thenReturn(getSingInResponse(true));
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/oauth/sign-in/{platform}/web", OAuthPlatform.KAKAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.token.accessToken").exists())
                    .andExpect(cookie().exists("refreshToken"));
        }
        
        @Test
        @DisplayName("실패 - 유효성 검증")
        void failValidation() throws Exception {
            // given
            SignInDto.OAuthRequest.Web request = SignInDto.OAuthRequest.Web.builder()
                    .code(" ")
                    .build();
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/oauth/sign-in/{platform}/web", OAuthPlatform.KAKAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.data.accessToken").doesNotExist())
                    .andExpect(jsonPath("$.errors.code").exists())
                    .andExpect(cookie().doesNotExist("refreshToken"));
        }
    }
    
    @Nested
    @DisplayName("소셜 로그인 - 모바일")
    class SignInOnMobile {
        private SignInDto.OAuthRequest.Mobile request = SignInDto.OAuthRequest.Mobile.builder()
                .accessToken("xxx.xxx.xxx")
                .build();
        
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            when(oAuthManagementService.signInOnMobile(any(OAuthPlatform.class), anyString()))
                    .thenReturn(getSingInResponse(true));
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/oauth/sign-in/{platform}/mobile", OAuthPlatform.KAKAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.token.accessToken").exists())
                    .andExpect(jsonPath("$.data.token.refreshToken").exists())
                    .andExpect(cookie().doesNotExist("refreshToken"));
        }
        
        @Test
        @DisplayName("실패 - 유효성 검증")
        void failValidation() throws Exception {
            // given
            SignInDto.OAuthRequest.Mobile request = SignInDto.OAuthRequest.Mobile.builder()
                    .accessToken(" ")
                    .build();
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/oauth/sign-in/{platform}/mobile", OAuthPlatform.KAKAO)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.data.accessToken").doesNotExist())
                    .andExpect(jsonPath("$.errors.accessToken").exists())
                    .andExpect(cookie().doesNotExist("refreshToken"));
        }
    }
}