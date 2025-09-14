package kr.co.yeogiga.application.settlement.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.settlement.dto.PayInfoDto;
import kr.co.yeogiga.domain.settlement.dto.SettlementDto;
import kr.co.yeogiga.domain.settlement.exception.SettlementErrorType;
import kr.co.yeogiga.domain.settlement.service.SettlementService;
import kr.co.yeogiga.domain.settlement.type.SettlementType;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettlementQueryServiceTest {
    @Mock
    private SettlementService settlementService;
    
    @Mock
    private TripMemberService tripMemberService;
    
    @InjectMocks
    private SettlementQueryService settlementQueryService;
    
    @Nested
    @DisplayName("정산 내역 조회")
    class GetSettlement {
        private final Long tripId = 1L;
        private final Long userId = 1L;
        private final Long settlementId = 1L;
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            PayInfoDto payInfoDto1 = new PayInfoDto(10L, userId, "nick1", "http://image.com/1", 10000L, true);
            PayInfoDto payInfoDto2 = new PayInfoDto(11L, 2L, "nick2", "http://image.com/2", 10000L, false);
            
            SettlementDto settlementDto = new SettlementDto(
                    settlementId,
                    "점심",
                    20000L,
                    LocalDate.now(),
                    SettlementType.MEAL,
                    userId,
                    false,
                    List.of(payInfoDto1, payInfoDto2)
            );
            
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            when(settlementService.findSettlementDtoById(settlementId))
                    .thenReturn(Optional.of(settlementDto));
            
            // when
            SettlementDto result = settlementQueryService.getSettlement(tripId, userId, settlementId);
            
            // then
            assertEquals(settlementId, result.id());
            assertEquals("점심", result.name());
            assertEquals(userId, result.payerId());
            assertFalse(result.isCompleted());
            assertEquals(10L, result.payers().get(0).id());
            assertEquals(11L, result.payers().get(1).id());
        }
        
        @Test
        @DisplayName("실패 - 여행 멤버가 아닌 경우")
        void failIfNotTripMember() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(false);
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementQueryService.getSettlement(tripId, userId, settlementId));
            
            // then
            assertEquals(TripMemberErrorType.IS_NOT_MEMBER, exception.getErrorType());
        }
        
        @Test
        @DisplayName("실패 - 정산 내역이 존재하지 않는 경우")
        void failIfSettlementNotFound() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            when(settlementService.findSettlementDtoById(settlementId)).thenReturn(Optional.empty());
            
            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> settlementQueryService.getSettlement(tripId, userId, settlementId));
            
            // then
            assertEquals(SettlementErrorType.NOT_FOUND, exception.getErrorType());
        }
    }
}
