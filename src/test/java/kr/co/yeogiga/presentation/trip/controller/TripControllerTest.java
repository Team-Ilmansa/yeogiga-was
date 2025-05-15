package kr.co.yeogiga.presentation.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.application.trip.service.TripCommandService;
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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripCommandService tripCommandService;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();

        User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("email")
                .role(Role.USER)
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        userDetails = new CustomUserDetailsImpl(user);
    }

    @Nested
    @DisplayName("여행 생성")
    class TripCreation {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            TripReq.Creation creationRequest = TripReq.Creation.builder()
                    .title("test")
                    .city("대구광역시")
                    .build();

            doNothing().when(tripCommandService).create(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsString(creationRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()));
        }

        @Test
        @DisplayName("유효성 검증 실패 - null")
        void failValidationNull() throws Exception {
            // given
            TripReq.Creation creationRequest = TripReq.Creation.builder()
//                    .title("test")
                    .city("대구광역시")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsString(creationRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        @DisplayName("유효성 검증 실패 - 최대 길이 초과")
        void failValidationLength() throws Exception {
            // given
            TripReq.Creation creationRequest = TripReq.Creation.builder()
                    .title("test")
                    .city("대구광역시대구광역시대구광역시대구광역시대구광역시대구광역시")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsString(creationRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.city").value("여행 도시는 최대 20글자까지 가능합니다."));
        }
    }

    @Nested
    @DisplayName("여행 시간 수정")
    class TimeModification {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 5, 2, 12, 00);
            TripReq.Time request = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            doNothing().when(tripCommandService).updateTime(any(), any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/time", 1L)
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
        @DisplayName("실패 - 유효성 검증 실패")
        void failValidation() throws Exception {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 5, 2, 12, 00);
            TripReq.Time request = TripReq.Time.builder()
                    .start(startTime)
//                    .end(endTime)
                    .build();

            doNothing().when(tripCommandService).updateTime(any(), any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/time", 1L)
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.end").exists());
        }
    }
}
