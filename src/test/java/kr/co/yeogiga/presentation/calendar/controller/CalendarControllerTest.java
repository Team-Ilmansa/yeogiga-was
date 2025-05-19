package kr.co.yeogiga.presentation.calendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.calendar.dto.CalendarReq;
import kr.co.yeogiga.application.calendar.dto.CalendarRes;
import kr.co.yeogiga.application.calendar.service.CalendarCommandService;
import kr.co.yeogiga.application.calendar.service.CalendarQueryService;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.common.response.success.SuccessResponse;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockBean
    private CalendarQueryService calendarQueryService;

    private CustomUserDetailsImpl userDetails;

    private final Long tripId = 1L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(get("/**").with(csrf()))
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
        ReflectionTestUtils.setField(user, "id", userId);
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()));
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
    @DisplayName("W2M 조회 테스트")
    class ReadCalendarTest {

        @Test
        @DisplayName("내 가능 날짜 조회 테스트")
        void getUserAvailabilityTest() throws Exception {
            // given
            CalendarRes.UserAvailability response =
                    new CalendarRes.UserAvailability(userId, List.of(LocalDate.of(2025, 7, 1)));

            given(calendarQueryService.getUserAvailability(any(), any())).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/calendars/me", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(response.userId()))
                    .andExpect(jsonPath("$.data.availableDates[0]").value("2025-07-01"));
        }

        @Test
        @DisplayName("Trip 전체 유저 가능 날짜 조회 테스트")
        void getTripAvailabilitiesTest() throws Exception {
            // given
            List<CalendarRes.UserAvailability> list = List.of(
                    new CalendarRes.UserAvailability(1L, List.of(LocalDate.of(2025, 7, 1))),
                    new CalendarRes.UserAvailability(2L, List.of(LocalDate.of(2025, 7, 2)))
            );
            CalendarRes.TripAvailabilityList response = new CalendarRes.TripAvailabilityList(list);

            given(calendarQueryService.getTripAvailabilities(tripId)).willReturn(response);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/calendars", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.availabilities", hasSize(2)))
                    .andExpect(jsonPath("$.data.availabilities[0].userId").value(1))
                    .andExpect(jsonPath("$.data.availabilities[1].availableDates[0]").value("2025-07-02"));
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
