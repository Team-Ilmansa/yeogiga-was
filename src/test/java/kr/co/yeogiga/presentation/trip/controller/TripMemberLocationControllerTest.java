package kr.co.yeogiga.presentation.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.trip.dto.TripMemberLocationDto;
import kr.co.yeogiga.application.trip.service.TripMemberLocationCommandService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripMemberLocationController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripMemberLocationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private TripMemberLocationCommandService tripMemberLocationCommandService;
    
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
    @DisplayName("멤버 위치 저장")
    class SaveLocation {
        private final Long tripId = 1L;
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            TripMemberLocationDto.Request request = new TripMemberLocationDto.Request(
                    1.1,
                    2.2
            );
            
            doNothing().when(tripMemberLocationCommandService).saveLocation(any(), any(), any());
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/members/location", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(SuccessResponse.created().code()))
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()));
        }
        
        @Test
        @DisplayName("실패 - 유효성 검증 실패: 위도, 경도 범위 초과")
        void failValidationLocationRange() throws Exception {
            // given
            TripMemberLocationDto.Request request = new TripMemberLocationDto.Request(
                    300.0,
                    300.0
            );
            
            doNothing().when(tripMemberLocationCommandService).saveLocation(anyLong(), any(), any());
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/members/location", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.latitude").exists())
                    .andExpect(jsonPath("$.errors.longitude").exists());
        }
        
        @Test
        @DisplayName("실패 - 여행이 존재하지 않거나 멤버가 아닌 경우")
        void failIfNotMember() throws Exception {
            // given
            TripMemberLocationDto.Request request = new TripMemberLocationDto.Request(
                    30.0,
                    120.0
            );
            
            doThrow(new CustomException(TripMemberErrorType.IS_NOT_MEMBER))
                    .when(tripMemberLocationCommandService).saveLocation(anyLong(), any(), any());
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/members/location", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(TripMemberErrorType.IS_NOT_MEMBER.getCode()))
                    .andExpect(jsonPath("$.message").value(TripMemberErrorType.IS_NOT_MEMBER.getMessage()));
                    
        }
    }
}
