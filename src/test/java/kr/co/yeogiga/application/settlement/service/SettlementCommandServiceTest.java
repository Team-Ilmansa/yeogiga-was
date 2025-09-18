package kr.co.yeogiga.application.settlement.service;

import kr.co.yeogiga.application.settlement.dto.SettlementRequest;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import kr.co.yeogiga.domain.settlement.entity.Settlement;
import kr.co.yeogiga.domain.settlement.exception.SettlementErrorType;
import kr.co.yeogiga.domain.settlement.service.PayInfoService;
import kr.co.yeogiga.domain.settlement.service.SettlementService;
import kr.co.yeogiga.domain.settlement.type.SettlementType;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettlementCommandServiceTest {
    @Mock
    private SettlementService settlementService;
    
    @Mock
    private PayInfoService payInfoService;
    
    @Mock
    private TripMemberService tripMemberService;
    
    @InjectMocks
    private SettlementCommandService settlementCommandService;
    
    @Nested
    @DisplayName("정산 내역 생성")
    class CreateSettlement {
        private final Long tripId = 10L;
        private final Long userId = 1L;
        
        private List<User> members = List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build()
        );
        
        private SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                .name("점심 식사")
                .totalPrice(50000L)
                .date(LocalDate.now())
                .type(SettlementType.RESTAURANT)
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
        
        @Captor
        ArgumentCaptor<Settlement> settlementCaptor;
        
        @Captor
        private ArgumentCaptor<List<PayInfo>> payInfosCaptor;
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            when(settlementService.save(any(Settlement.class))).thenReturn(100L);
            doNothing().when(payInfoService).saveAllInBatch(anyList());
            
            // when
            settlementCommandService.createSettlement(tripId, userId, settlementDto);
            
            // then
            verify(settlementService, times(1)).save(settlementCaptor.capture());
            verify(payInfoService, times(1)).saveAllInBatch(payInfosCaptor.capture());
            
            assertEquals(false, settlementCaptor.getValue().isCompleted());
            payInfosCaptor.getValue().forEach(payInfo -> assertEquals(100L, payInfo.getSettlementId()));
        }
        
        @Test
        @DisplayName("성공 - 모든 멤버가 정산이 완료된 경우 해당 정산 내역이 완료가 되는가")
        void successIfAllCompleted() {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.now())
                    .type(SettlementType.RESTAURANT)
                    .payers(List.of(
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(1L)
                                    .price(10000L)
                                    .isCompleted(true)
                                    .build(),
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(2L)
                                    .price(40000L)
                                    .isCompleted(true)
                                    .build()
                    ))
                    .build();
            
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            when(settlementService.save(any(Settlement.class))).thenReturn(100L);
            doNothing().when(payInfoService).saveAllInBatch(anyList());
            
            // when
            settlementCommandService.createSettlement(tripId, userId, settlementDto);
            
            // then
            verify(settlementService, times(1)).save(settlementCaptor.capture());
            verify(payInfoService, times(1)).saveAllInBatch(anyList());
            
            assertEquals(true, settlementCaptor.getValue().isCompleted());
        }
        
        @Test
        @DisplayName("실패 - 여행 미존재")
        void failIfTripNotFound() {
            // given
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(Collections.emptyList());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.createSettlement(tripId, userId, settlementDto));
            
            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 정산자에 여행 멤버가 아닌 사용자가 포함된 경우")
        void failIfExistsNotTripMember() {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.now())
                    .type(SettlementType.RESTAURANT)
                    .payers(List.of(
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(1L)
                                    .price(10000L)
                                    .isCompleted(true)
                                    .build(),
                            SettlementRequest.PayInfoDto.builder()
                                    .userId(3L)
                                    .price(40000L)
                                    .isCompleted(false)
                                    .build()
                    ))
                    .build();
            
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.createSettlement(tripId, userId, settlementDto));
            
            // then
            assertEquals(TripMemberErrorType.EXISTS_NOT_MEMBER, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 정산 내역 총합 불일치")
        void failIfTotalPriceNotEquals() {
            // given
            SettlementRequest.SettlementDto settlementDto = SettlementRequest.SettlementDto.builder()
                    .name("점심 식사")
                    .totalPrice(50000L)
                    .date(LocalDate.now())
                    .type(SettlementType.RESTAURANT)
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
            
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(members);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementCommandService.createSettlement(tripId, userId, settlementDto));
            
            // then
            assertEquals(SettlementErrorType.NOT_VALID_PRICE, exception.getErrorType());
        }
    }
}
