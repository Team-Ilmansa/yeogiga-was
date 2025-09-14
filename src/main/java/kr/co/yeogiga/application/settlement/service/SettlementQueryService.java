package kr.co.yeogiga.application.settlement.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.settlement.dto.SettlementDto;
import kr.co.yeogiga.domain.settlement.exception.SettlementErrorType;
import kr.co.yeogiga.domain.settlement.service.SettlementService;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementQueryService {
    private final SettlementService settlementService;
    private final TripMemberService tripMemberService;
    
    /**
     * 정산 내역을 조회하는 메서드
     *
     * @param tripId        여행 ID
     * @param userId        사용자 ID
     * @param settlementId  정산 내역 ID
     * @return              정산 내역
     *
     * @throws CustomException TripMemberErrorType.IS_NOT_MEMBER - 여행 멤버가 아닌 사용자가 요청을 보낸 경우
     * @throws CustomException SettlementErrorType.NOT_FOUND - 정산 내역이 존재하지 않는 경우
     */
    public SettlementDto getSettlement(Long tripId, Long userId, Long settlementId) {
        if (!tripMemberService.existsByTripIdAndUserId(tripId, userId)) {
            throw new CustomException(TripMemberErrorType.IS_NOT_MEMBER);
        }
        
        return settlementService.findSettlementDtoById(settlementId)
                .orElseThrow(() -> new CustomException(SettlementErrorType.NOT_FOUND));
    }
    
}
