package kr.co.yeogiga.presentation.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.constant.AuthConstants;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.application.user.dto.UserInfoRes;
import kr.co.yeogiga.application.user.dto.UserInfoUpdateReq;
import kr.co.yeogiga.application.user.service.UserManagementService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
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
                .defaultRequest(delete("/**").with(csrf()))
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

    @Nested
    @DisplayName("회원 탈퇴")
    class Withdraw {
        private final Long userId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(userManagementService).withdraw(userId);
            setUpUserDetails(Role.USER);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/users")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(cookie().maxAge(AuthConstants.REFRESH_TOKEN_PREFIX, 0))
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 접근 권한 오류")
        void failAuthorization() throws Exception {
            // given
            doNothing().when(userManagementService).withdraw(userId);
            setUpUserDetails(Role.GUEST);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/users")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(AuthErrorType.UN_REGISTERED_USER.getMessage()));
        }

        @Test
        @DisplayName("실패 - 이미 탈퇴한 사용자")
        void failAlreadyWithdraw() throws Exception {
            // given
            doThrow(new CustomException(UserErrorType.ALREADY_WITHDRAW)).when(userManagementService).withdraw(any());
            setUpUserDetails(Role.USER);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/users")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(UserErrorType.ALREADY_WITHDRAW.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.ALREADY_WITHDRAW.getMessage()));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void failNotFound() throws Exception {
            // given
            doThrow(new CustomException(UserErrorType.NOT_FOUND)).when(userManagementService).withdraw(any());
            setUpUserDetails(Role.USER);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/users")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(UserErrorType.NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("회원 정보 조회")
    class GetUserInfo {
        @Test
        @DisplayName("성공 - 소셜 로그인 사용자")
        void successForSocialUser() throws Exception {
            // given
            setUpUserDetails(Role.USER);
            UserInfoRes userInfoRes = UserInfoRes.builder()
                    .nickname("nickname")
                    .email("test@test.com")
                    .build();
            when(userManagementService.getUserInfo(any())).thenReturn(userInfoRes);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/users/my")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nickname").value("nickname"))
                    .andExpect(jsonPath("$.data.email").value("test@test.com"))
                    .andExpect(jsonPath("$.data.username").doesNotExist());
        }

        @Test
        @DisplayName("성공 - 일반 로그인 사용자")
        void successForNormalUser() throws Exception {
            // given
            setUpUserDetails(Role.USER);
            UserInfoRes userInfoRes = UserInfoRes.builder()
                    .username("username")
                    .nickname("nickname")
                    .email("test@test.com")
                    .build();
            when(userManagementService.getUserInfo(any())).thenReturn(userInfoRes);


            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/users/my")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.nickname").value("nickname"))
                    .andExpect(jsonPath("$.data.email").value("test@test.com"))
                    .andExpect(jsonPath("$.data.username").value("username"));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void failUserNotFound() throws Exception {
            // given
            doThrow(new CustomException(UserErrorType.NOT_FOUND)).when(userManagementService).getUserInfo(any());
            setUpUserDetails(Role.USER);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/users/my")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(UserErrorType.NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("회원 정보 수정")
    class UpdateUserInfo {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            UserInfoUpdateReq userInfoUpdateReq = UserInfoUpdateReq.builder()
                    .nickname("newNickname")
                    .email("newTest@test.com")
                    .build();
            setUpUserDetails(Role.USER);

            doNothing().when(userManagementService).updateUserInfo(userDetails.getUserId(), userInfoUpdateReq);

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/users")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(userInfoUpdateReq))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(SuccessResponse.ok().code()))
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));

        }

        @Test
        @DisplayName("실패 - 이미 사용 중인 닉네임")
        void failAlreadyUsedNickname() throws Exception {
            // given
            UserInfoUpdateReq userInfoUpdateReq = UserInfoUpdateReq.builder()
                    .nickname("newNickname")
                    .email("newTest@test.com")
                    .build();
            setUpUserDetails(Role.USER);

            doThrow(new CustomException(UserErrorType.ALREADY_USED_NICKNAME)).when(userManagementService).updateUserInfo(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/users")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(userInfoUpdateReq))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(UserErrorType.ALREADY_USED_NICKNAME.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.ALREADY_USED_NICKNAME.getMessage()));
        }

        @Test
        @DisplayName("실패 - 기존과 동일한 닉네임")
        void failSameNickname() throws Exception {
            // given
            UserInfoUpdateReq userInfoUpdateReq = UserInfoUpdateReq.builder()
                    .nickname("newNickname")
                    .email("newTest@test.com")
                    .build();
            setUpUserDetails(Role.USER);

            doThrow(new CustomException(UserErrorType.SAME_NICKNAME)).when(userManagementService).updateUserInfo(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/users")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(userInfoUpdateReq))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(UserErrorType.SAME_NICKNAME.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.SAME_NICKNAME.getMessage()));
        }

        @Test
        @DisplayName("실패 - 기존과 동일한 이메일")
        void failSameEmail() throws Exception {
            // given
            UserInfoUpdateReq userInfoUpdateReq = UserInfoUpdateReq.builder()
                    .nickname("newNickname")
                    .email("newTest@test.com")
                    .build();
            setUpUserDetails(Role.USER);

            doThrow(new CustomException(UserErrorType.SAME_EMAIL)).when(userManagementService).updateUserInfo(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/users")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(userInfoUpdateReq))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(UserErrorType.SAME_EMAIL.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.SAME_EMAIL.getMessage()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증 실패")
        void failValidation() throws Exception {
            // given
            UserInfoUpdateReq userInfoUpdateReq = UserInfoUpdateReq.builder()
//                    .nickname("newNickname")
//                    .email("newTest@test.com")
                    .build();
            setUpUserDetails(Role.USER);

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/users")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(userInfoUpdateReq))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(CommonErrorType.VALIDATION_ERROR.getCode()))
                    .andExpect(jsonPath("$.errors.nickname").exists())
                    .andExpect(jsonPath("$.errors.email").exists());
        }
    }
}
