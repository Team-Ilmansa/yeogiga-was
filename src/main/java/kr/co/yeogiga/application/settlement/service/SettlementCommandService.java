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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
        
        if (!dto.isValidPrice()) {
            throw new CustomException(SettlementErrorType.NOT_VALID_PRICE);
        }
        
        
        Settlement settlement = dto.toEntity(tripId, userId);
        
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
     * 정산 내역을 갱신하는 메서드
     *
     * @param tripId        여행 ID
     * @param userId        사용자 ID
     * @param settlementId  정산 내역 ID
     * @param dto           정산 내역 갱신 요청 DTO
     *
     * @throws CustomException TripErrorType.TRIP_NOT_FOUND - 여행이 존재하지 않는 경우
     * @throws CustomException SettlementErrorType.IS_NOT_PAYER - 해당 정산 내역의 작성자가 아닌 경우
     * @throws CustomException TripMemberErrorType.EXISTS_NOT_MEMBER - 여행 멤버가 아닌 분담자가 존재하는 경우
     * @throws CustomException SettlementErrorType.NOT_VALID_PRICE - 정산 내역 금액의 총합이 일치하지 않는 경우
     */
    @Transactional
    public void updateSettlement(Long tripId, Long userId, Long settlementId, SettlementRequest.SettlementDto dto) {
        List<User> members = tripMemberService.readAllUserByTripId(tripId);
        
        if (members.isEmpty()) {
            throw new CustomException(TripErrorType.TRIP_NOT_FOUND);
        }
        
        Settlement settlement = settlementService.readById(settlementId)
                .orElseThrow(() -> new CustomException(SettlementErrorType.NOT_FOUND));
        
        if (!settlement.isPayer(userId)) {
            throw new CustomException(SettlementErrorType.IS_NOT_PAYER);
        }
        
        List<SettlementRequest.PayInfoDto> payers = dto.payers();
        
        if (!isAllTripMember(payers, members)) {
            throw new CustomException(TripMemberErrorType.EXISTS_NOT_MEMBER);
        }
        
        if (!dto.isValidPrice()) {
            throw new CustomException(SettlementErrorType.NOT_VALID_PRICE);
        }
     
        // TODO: Settlement - PayInfo 연관관계 설정 후 수정 예정
        List<PayInfo> payInfos = payInfoService.readAllBySettlementId(settlementId);
        
        settlement.update(dto.name(), dto.totalPrice(), dto.date(), dto.type());
        synchronizePayInfo(payInfos, payers, settlementId);
    }
    
    /**
     * 정산 내역 수정 시 인당 분담 내역({@code PayInfo}) 추가, 수정, 삭제를 처리하는 메서드
     *
     * @param oldPayInfos   기존의 인당 분담 내역 목록
     * @param newPayInfos   수정할 인당 분담 내역 목록
     * @param settlementId  정산 내역 ID
     */
    private void synchronizePayInfo(
            List<PayInfo> oldPayInfos,
            List<SettlementRequest.PayInfoDto> newPayInfos,
            Long settlementId
    ) {
        Map<Long, SettlementRequest.PayInfoDto> newPayInfoMap = newPayInfos.stream()
                .collect(Collectors.toMap(
                        payInfoDto -> payInfoDto.userId(),
                        Function.identity()
                ));
        
        addPayInfo(oldPayInfos, newPayInfos, settlementId);
        updatePayInfo(oldPayInfos, newPayInfoMap);
        deletePayInfo(oldPayInfos, newPayInfoMap);
    }
    
    /**
     * 분담 내역 수정 후, 삭제할 분담 내역을 처리하는 메서드
     *
     * @param oldPayInfos   기존의 인당 분담 내역 목록
     * @param newPayInfoMap 분담자 ID를 Key로 가지는 인당 분담 내역 Map
     */
    private void deletePayInfo(List<PayInfo> oldPayInfos, Map<Long, SettlementRequest.PayInfoDto> newPayInfoMap) {
        List<Long> deletedPayInfoIds = oldPayInfos.stream()
                .filter(payInfo -> !newPayInfoMap.containsKey(payInfo.getUserId()))
                .map(PayInfo::getId)
                .toList();
        
        if (!deletedPayInfoIds.isEmpty()) {
            payInfoService.deleteByIds(deletedPayInfoIds);
        }
    }
    
    /**
     * 분담 내역 수정 후, 갱신할 분담 내역을 처리하는 메서드
     *
     * @param oldPayInfos   기존의 인당 분담 내역 목록
     * @param newPayInfoMap 분담자 ID를 Key로 가지는 인당 분담 내역 Map
     */
    private void updatePayInfo(List<PayInfo> oldPayInfos, Map<Long, SettlementRequest.PayInfoDto> newPayInfoMap) {
        oldPayInfos.forEach(payInfo -> {
            SettlementRequest.PayInfoDto payInfoDto = newPayInfoMap.get(payInfo.getUserId());
            
            if (payInfoDto != null) {
                payInfo.update(payInfoDto.price());
            }
        });
    }
    
    /**
     * 분담 내역 수정 후, 추가할 분담 내역을 처리하는 메서드
     *
     * @param oldPayInfos   기존의 인당 분담 내역 목록
     * @param newPayInfos   새로운 인당 분담 내역 목록
     * @param settlementId  정산 내역 ID
     */
    private void addPayInfo(List<PayInfo> oldPayInfos, List<SettlementRequest.PayInfoDto> newPayInfos, Long settlementId) {
        List<Long> oldPayers = oldPayInfos.stream()
                .map(payInfo -> payInfo.getUserId())
                .toList();
        
        List<PayInfo> addedPayInfos = newPayInfos.stream()
                .filter(payInfoDto -> !oldPayers.contains(payInfoDto.userId()))
                .map(payInfoDto -> payInfoDto.toEntity(settlementId))
                .toList();
        
        if (!addedPayInfos.isEmpty()) {
            payInfoService.saveAllInBatch(addedPayInfos);
        }
    }
    
    /**
     * 정산 여부를 갱신하는 메서드
     *
     * <p> 모든 분담 내역이 정산 완료된 경우, 해당 분담 내역을 포함하는 정산 내역도 정산 완료 처리
     *
     * @param settlementId  정산 내역 ID
     * @param userId        사용자 ID
     * @param dtos          정산 여부 갱신 DTO 리스트
     *
     * @throws CustomException SettlementErrorType.NOT_FOUND - 정산 내역이 존재하지 않는 경우
     * @throws CustomException SettlementErrorType.IS_NOT_PAYER - 요청자가 정산 내역의 생성자가 아닌 경우
     * @throws CustomException SettlementErrorTYpe.PAY_INFO_NOT_FOUND - 분담 내역이 존재하지 않는 경우
     */
    @Transactional
    public void completeSettlement(Long settlementId, Long userId, List<SettlementRequest.PayInfoCompletionDto> dtos) {
        Settlement settlement = settlementService.readById(settlementId)
                .orElseThrow(() -> new CustomException(SettlementErrorType.NOT_FOUND));
        
        if (!settlement.isPayer(userId)) {
            throw new CustomException(SettlementErrorType.IS_NOT_PAYER);
        }
        
        List<PayInfo> payInfos = payInfoService.readAllBySettlementId(settlementId);
        Map<Long, PayInfo> payInfoMap = payInfos.stream()
                .collect(Collectors.toMap(
                        payInfo -> payInfo.getId(),
                        Function.identity()
                ));
        
        boolean isAllCompleted = true;
        
        for (SettlementRequest.PayInfoCompletionDto dto : dtos) {
            PayInfo payInfo = payInfoMap.get(dto.payInfoId());
            
            if (payInfo == null) {
                throw new CustomException(SettlementErrorType.PAY_INFO_NOT_FOUND);
            }
            
            if (dto.isCompleted()) {
                payInfo.complete();
            } else {
                payInfo.uncomplete();
            }
            
            isAllCompleted = isAllCompleted && dto.isCompleted();
        }
        
        if (isAllCompleted) {
            settlement.complete();
        } else {
            settlement.uncomplete();
        }
    }
    
    /**
     * 정산 내역을 삭제하는 메서드
     *
     * @param tripId        여행 ID
     * @param userId        사용자 ID
     * @param settlementId  정산 내역 ID
     *
     * @throws CustomException TripMemberErrorType.IS_NOT_MEMBER - 요청자가 여행 멤버가 아닐 경우
     * @throws CustomException SettlementErrorType.NOT_FOUND - 정산 내역이 존재하지 않을 경우
     * @throws CustomException SettlementErrorType.IS_NOT_PAYER - 요청자가 정산 생성자가 아닐 경우
     */
    @Transactional
    public void deleteSettlement(Long tripId, Long userId, Long settlementId) {
        if (!tripMemberService.existsByTripIdAndUserId(tripId, userId)) {
            throw new CustomException(TripMemberErrorType.IS_NOT_MEMBER);
        }
        
        Long payerId = settlementService.readPayerIdById(settlementId)
                .orElseThrow(() -> new CustomException(SettlementErrorType.NOT_FOUND));
        
        if (!payerId.equals(userId)) {
            throw new CustomException(SettlementErrorType.IS_NOT_PAYER);
        }
        
        payInfoService.deleteBySettlementId(settlementId);
        settlementService.deleteById(settlementId);
    }
}
