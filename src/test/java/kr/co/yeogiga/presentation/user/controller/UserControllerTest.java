package kr.co.yeogiga.presentation.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.application.user.service.UserManagementService;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import kr.co.yeogiga.infrastructure.TestSecurityConfig;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
@Import({TestSecurityConfig.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserManagementService userManagementService;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .apply(springSecurity())
                .defaultRequest(patch("/**").with(csrf()))
                .build();
    }

    void setUpUserDetails(Role role) {
        User user = User.builder()
                .username("test")
                .password("password")
                .email("test@test.com")
                .role(role)
                .nickname("nickname")
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        userDetails = new CustomUserDetailsImpl(user);
    }

    @Nested
    @DisplayName("비밀번호 갱신")
    class UpdatePassword {

        private final Long userId = 1L;

        private PasswordUpdateReq request = new PasswordUpdateReq("originalPassword", "newPassword");

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            setUpUserDetails(Role.USER);
            doNothing().when(userManagementService).updatePassword(userId, request);

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/users/password")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 접근 권한 오류")
        void failUnAuthorization() throws Exception {
            // given
            setUpUserDetails(Role.GUEST);

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/users/password")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(AuthErrorType.UN_REGISTERED_USER.getMessage()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증 실패")
        void failValidation() throws Exception {
            // given
            setUpUserDetails(Role.USER);
            doNothing().when(userManagementService).updatePassword(userId, request);
            PasswordUpdateReq request = new PasswordUpdateReq(" ", "  ");

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/users/password")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(CommonErrorType.VALIDATION_ERROR.getCode()))
                    .andExpect(jsonPath("$.errors").exists());
        }
    }
}
