package kr.co.yeogiga.presentation.pin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.pin.dto.PinReq;
import kr.co.yeogiga.application.pin.service.PinCommandService;
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

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PinController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class PinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PinCommandService pinCommandService;

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
    @DisplayName("핀 생성")
    class PinCreation {
        private final Long tripId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            PinReq.Creation request = PinReq.Creation.builder()
                    .place("대구광역시 달서구")
                    .latitude(1.1)
                    .longitude(2.2)
                    .time(LocalDateTime.of(2025, 5, 26, 13, 0))
                    .build();

            doNothing().when(pinCommandService).createPin(tripId, request);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/pin", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(SuccessResponse.created().code()))
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()));
        }

        @Test
        @DisplayName("실패 - 위도 경도 유효성 검증 실패: 범위 초과")
        void failIfLocationRangeException() throws Exception {
            // given
            PinReq.Creation request = PinReq.Creation.builder()
                    .place("대구광역시 달서구")
                    .latitude(200.0)
                    .longitude(200.0)
                    .time(LocalDateTime.of(2025, 5, 26, 13, 0))
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/pin", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }


        @Test
        @DisplayName("실패 - 위도 경도 유효성 검증 실패: null")
        void failIfLocationValidationException() throws Exception {
            // given
            PinReq.Creation request = PinReq.Creation.builder()
                    .place("대구광역시 달서구")
                    .latitude(200.0)
//                    .longitude(200.0)
                    .time(LocalDateTime.of(2025, 5, 26, 13, 0))
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/pin", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
    }
}
