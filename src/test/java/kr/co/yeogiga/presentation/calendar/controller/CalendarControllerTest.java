package kr.co.yeogiga.presentation.calendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.calendar.dto.CalendarReq;
import kr.co.yeogiga.application.calendar.service.CalendarCommandService;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CalendarController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                SecurityConfig.class, JwtAuthenticationFilter.class
        })
)
public class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalendarCommandService calendarCommandService;

    private CustomUserDetails userDetails;

    private final Long tripId = 1L;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(put("/**").with(csrf()))
                .build();

        setUpUserDetails();
    }

    void setUpUserDetails() {
        User user = User.builder()
                .username("testuser")
                .password("pass")
                .email("test@example.com")
                .nickname("nickname")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        userDetails = new CustomUserDetailsImpl(user);
    }

    @Nested
    @DisplayName("W2M 생성 테스트")
    class CreateCalendar {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            CalendarReq request = new CalendarReq(List.of(LocalDate.of(2025, 7, 1)));

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/calendars", tripId)
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증 (빈 날짜 리스트)")
        void failValidation() throws Exception {
            // given
            CalendarReq request = new CalendarReq(List.of());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/calendars", tripId)
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
    @DisplayName("W2M 수정 테스트")
    class UpdateCalendar {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            CalendarReq request = new CalendarReq(List.of(LocalDate.of(2025, 7, 2)));

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/calendars", tripId)
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
        @DisplayName("실패 - 유효성 검증 (날짜 없음)")
        void failValidation() throws Exception {
            // given
            CalendarReq request = new CalendarReq(List.of()); // 빈 리스트

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/calendars", tripId)
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
