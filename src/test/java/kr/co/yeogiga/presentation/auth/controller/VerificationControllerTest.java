package kr.co.yeogiga.presentation.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.dto.VerificationCodeDto;
import kr.co.yeogiga.application.auth.service.VerificationCodeService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = VerificationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class VerificationControllerTest {
    @MockBean
    private VerificationCodeService verificationCodeService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    
    private final String email = "test@test.com";
    private final String code = "123456";
    
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
    }
    
    @Nested
    @DisplayName("이메일 인증 코드 발송 요청")
    class SendEmailVerificationCode {
        
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(verificationCodeService).issueCode(anyString());
            VerificationCodeDto.SendRequest body = new VerificationCodeDto.SendRequest(email);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/email-verification/request")
                            .content(objectMapper.writeValueAsBytes(body))
                            .contentType(MediaType.APPLICATION_JSON)
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
            doNothing().when(verificationCodeService).issueCode(anyString());
            VerificationCodeDto.SendRequest body = new VerificationCodeDto.SendRequest(null);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/email-verification/request")
                            .content(objectMapper.writeValueAsBytes(body))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }
    }
    
    @Nested
    @DisplayName("이메일 인증 코드 검증")
    class VerifyEmailVerificationCode {
        private VerificationCodeDto.VerificationRequest body
                = new VerificationCodeDto.VerificationRequest(email, code);
    
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(verificationCodeService).verifyCode(anyString(), anyString());
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/email-verification/verify")
                            .content(objectMapper.writeValueAsBytes(body))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            
            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("인증에 성공하였습니다."));
        }
    
        @Test
        @DisplayName("실패 - 인증 시간 초과 또는 저장소 내 미존재 이메일")
        void failIfVerificationTimeout () throws Exception {
            // given
            doThrow(new CustomException(AuthErrorType.EMAIL_VERIFICATION_TIMEOUT))
                    .when(verificationCodeService).verifyCode(anyString(), anyString());
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/email-verification/verify")
                            .content(objectMapper.writeValueAsBytes(body))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(AuthErrorType.EMAIL_VERIFICATION_TIMEOUT.getCode()));
        }
        
        @Test
        @DisplayName("실패 - 인증 코드 불일치")
        void failIfCodeMismatched () throws Exception {
            // given
            doThrow(new CustomException(AuthErrorType.EMAIL_VERIFICATION_CODE_MISMATCH))
                    .when(verificationCodeService).verifyCode(anyString(), anyString());
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/email-verification/verify")
                            .content(objectMapper.writeValueAsBytes(body))
                            .contentType(MediaType.APPLICATION_JSON)
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(AuthErrorType.EMAIL_VERIFICATION_CODE_MISMATCH.getCode()));
        }
    }
}
