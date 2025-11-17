package kr.co.yeogiga.presentation.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.dto.PasswordResetDto;
import kr.co.yeogiga.application.auth.service.PasswordManagementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
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

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PasswordController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class PasswordControllerTest {
    private MockMvc mockMvc;
    
    @MockBean
    private PasswordManagementService passwordManagementService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
    }
    
    @Nested
    @DisplayName("비밀번호 초기화 요청")
    class RequestPasswordReset {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String email = "test@test.com";
            String username = "test";
            
            PasswordResetDto.Request request = new PasswordResetDto.Request(email, username);
            doNothing().when(passwordManagementService).requestPasswordReset(email, username);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/password/reset")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }
        
        @Test
        @DisplayName("실패 - 유효성 검증")
        void failValidation() throws Exception {
            // given
            String email = "";
            String username = "test";
            PasswordResetDto.Request request = new PasswordResetDto.Request(email, username);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/password/reset")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists())
                    .andExpect(jsonPath("$.errors.username").doesNotExist());
        }
    }
    
    @Nested
    @DisplayName("비밀번호 초기화")
    class ResetPassword {
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            String email = "test@test.com";
            String username = "test";
            String code = "code";
            String password = "password";
            PasswordResetDto.Reset request = new PasswordResetDto.Reset(email, username, code, password);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/auth/password/reset")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }
        
        @Test
        @DisplayName("실패 - 유효성 검증 실패")
        void failValidation() throws Exception {
            // given
            String email = "test@testcom";
            String username = "";
            String code = "code";
            String password = "password";
            PasswordResetDto.Reset request = new PasswordResetDto.Reset(email, username, code, password);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/auth/password/reset")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists())
                    .andExpect(jsonPath("$.errors.username").exists())
                    .andExpect(jsonPath("$.errors.code").doesNotExist())
                    .andExpect(jsonPath("$.errors.password").doesNotExist());
        }
    }
}
