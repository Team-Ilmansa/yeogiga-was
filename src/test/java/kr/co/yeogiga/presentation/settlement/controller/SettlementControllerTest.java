package kr.co.yeogiga.presentation.settlement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.settlement.dto.SettlementRequest;
import kr.co.yeogiga.application.settlement.service.SettlementCommandService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.settlement.exception.SettlementErrorType;
import kr.co.yeogiga.domain.settlement.type.SettlementType;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = SettlementController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class SettlementControllerTest {
    
    private MockMvc mockMvc;
    
    private CustomUserDetails userDetails;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private SettlementCommandService settlementCommandService;
    
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
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
    @DisplayName("정산 내역 생성")
    class CreateSettlement {
        private final Long tripId = 1L;
        
        private SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                .name("점심 식사")
                .totalPrice(50000L)
                .date(LocalDate.now())
                .type(SettlementType.MEAL)
                .payers(List.of(
                        SettlementRequest.PayInfoDto.builder()
                                .userId(1L)
                                .price(10000L)
                                .isCompleted(true)
                                .build(),
                        SettlementRequest.PayInfoDto.builder()
                                .userId(2L)
                                .price(40000L)
                                .isCompleted(false)
                                .build()
                ))
                .build();
        
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(settlementCommandService).createSettlement(tripId, userDetails.getUserId(), settlementDto);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/settlement/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(settlementDto))
            );
            
            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()));
        }
        
        @Test
        @DisplayName("실패 - 유효성 검증")
        void failValidation() throws Exception {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("  ")
                    .totalPrice(-1L)
                    .date(LocalDate.now())
                    .type(SettlementType.MEAL)
                    .payers(List.of(
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(1L)
                                    .price(-2L)
                                    .isCompleted(true)
                                    .build(),
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(2L)
                                    .price(40000L)
                                    .isCompleted(false)
                                    .build()
                    ))
                    .build();
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/settlement/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(settlementDto))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists())
                    .andExpect(jsonPath("$.errors.totalPrice").exists())
                    .andExpect(jsonPath("$.errors.['payers[0].price']").exists());
            
        }
        
        @Test
        @DisplayName("실패 - 여행 미존재")
        void failIfTripNotFound() throws Exception {
            // given
            doThrow(new CustomException(TripErrorType.TRIP_NOT_FOUND)).when(settlementCommandService)
                    .createSettlement(tripId, userDetails.getUserId(), settlementDto);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/settlement/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(settlementDto))
            );
            
            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(TripErrorType.TRIP_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.TRIP_NOT_FOUND.getMessage()));
        }
        
        @Test
        @DisplayName("실패 - 정산자에 여행 멤버가 아닌 사용자가 포함된 경우")
        void failIfExistsNotTripMember() throws Exception {
            // given
            doThrow(new CustomException(TripMemberErrorType.EXISTS_NOT_MEMBER)).when(settlementCommandService)
                    .createSettlement(tripId, userDetails.getUserId(), settlementDto);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/settlement/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(settlementDto))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(TripMemberErrorType.EXISTS_NOT_MEMBER.getCode()))
                    .andExpect(jsonPath("$.message").value(TripMemberErrorType.EXISTS_NOT_MEMBER.getMessage()));
        }
        
        @Test
        @DisplayName("실패 - 정산 내역 총합 불일치")
        void failIfTotalPriceNotEquals() throws Exception {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.now())
                    .type(SettlementType.MEAL)
                    .payers(List.of(
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(1L)
                                    .price(10000L)
                                    .isCompleted(true)
                                    .build(),
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(2L)
                                    .price(20000L)
                                    .isCompleted(false)
                                    .build()
                    ))
                    .build();
            
            doThrow(new CustomException(SettlementErrorType.NOT_VALID_PRICE)).when(settlementCommandService)
                    .createSettlement(tripId, userDetails.getUserId(), settlementDto);
            
            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/settlement/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(settlementDto))
            );
            
            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(SettlementErrorType.NOT_VALID_PRICE.getCode()))
                    .andExpect(jsonPath("$.message").value(SettlementErrorType.NOT_VALID_PRICE.getMessage()));
        }
    }
}
