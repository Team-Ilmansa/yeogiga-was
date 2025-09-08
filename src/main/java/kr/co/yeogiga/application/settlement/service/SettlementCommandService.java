package kr.co.yeogiga.application.settlement.service;

import kr.co.yeogiga.application.settlement.dto.SettlementRequest;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import kr.co.yeogiga.domain.settlement.entity.Settlement;
import kr.co.yeogiga.domain.settlement.exception.SettlementErrorType;
import kr.co.yeogiga.domain.settlement.service.PayInfoService;
import kr.co.yeogiga.domain.settlement.service.SettlementService;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementCommandService {
    private final SettlementService settlementService;
    private final PayInfoService payInfoService;
    private final TripMemberService tripMemberService;

    /**
     * 정산 내역을 생성하는 메서드
     *
     * @param tripId    여행 ID
     * @param userId    작성자 ID
     * @param dto       정산내역 생성 요청 DTO
     *
     *
     * @throws CustomException  {@code TripErrorType.TRIP_NOT_FOUND} - 존재하지 않는 여행일 경우
     *                          {@code TripMemberErrorType.EXISTS_NOT_MEMBER} - 여행 멤버가 아닌 정산자가 존재하는 경우
     *                          {@code SettlementErrorType.NOT_VALID_PRICE} - 정산 내역 금액의 총합이 일치하지 않는 경우
     */
    @Transactional
    public void createSettlement(Long tripId, Long userId, SettlementRequest.SettlementDto dto) {
        List<User> members = tripMemberService.readAllUserByTripId(tripId);
        
        if (members.isEmpty()) {
            throw new CustomException(TripErrorType.TRIP_NOT_FOUND);
        }
        
        List<SettlementRequest.PayInfoDto> payers = dto.payers();
        
        if (!isAllTripMember(payers, members)) {
            throw new CustomException(TripMemberErrorType.EXISTS_NOT_MEMBER);
        }
        
        if (!isValidPrice(payers, dto.totalPrice())) {
            throw new CustomException(SettlementErrorType.NOT_VALID_PRICE);
        }
        
        
        Settlement settlement = dto.toEntity(tripId, userId, isAllCompleted(payers));
        
        Long settlementId = settlementService.save(settlement);
        
        List<PayInfo> payInfoList = payers.stream()
                .map(payInfo -> payInfo.toEntity(settlementId))
                .toList();
        
        payInfoService.saveAllInBatch(payInfoList);
    }
    
    /**
     * 정산할 인원이 모두 여행의 멤버인지 여부를 반환하는 메서드
     *
     * @param payers    인원 당 정산 내역 목록
     * @param members   여행 멤버 목록
     * @return          정산할 인원이 모두 여행의 멤버인지 여부
     */
    private boolean isAllTripMember(List<SettlementRequest.PayInfoDto> payers, List<User> members) {
        Set<Long> memberIds = members.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        
        return payers.stream()
                .allMatch(payer -> memberIds.contains(payer.userId()));
    }
    
    /**
     * 인원 별 정산 금액의 총합이 정산 내역의 총합과 일치하는지 여부를 반환하는 메서드
     *
     * @param payers        인원 당 정산 내역 목록
     * @param totalPrice    정산 내역 총합
     * @return              인원 별 정산 금액의 총합과 정산 내역의 총합이 일치하는지 여부
     */
    private boolean isValidPrice(List<SettlementRequest.PayInfoDto> payers, Long totalPrice) {
        Long sum = payers.stream()
                .mapToLong(SettlementRequest.PayInfoDto::price)
                .sum();
        
        return sum.equals(totalPrice);
    }
    
    /**
     * 인원 당 정산 내역 정보가 모두 정산이 완료되었는지 여부를 반환하는 메서드
     *
     * @param payers    인원 당 정산 내역 정보 리스트
     * @return          리스트 내 모든 정산 내역이 정산이 완료되었는지 여부
     */
    private boolean isAllCompleted(List<SettlementRequest.PayInfoDto> payers) {
        return payers.stream()
                .allMatch(SettlementRequest.PayInfoDto::isCompleted);
    }
}
