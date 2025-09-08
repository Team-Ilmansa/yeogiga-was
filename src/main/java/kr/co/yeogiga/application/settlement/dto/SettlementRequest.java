package kr.co.yeogiga.application.settlement.dto;

import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import kr.co.yeogiga.domain.settlement.entity.Settlement;
import kr.co.yeogiga.domain.settlement.type.SettlementType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class SettlementRequest {
    
    @Builder
    public record SettlementDto (
            String name,
            Long totalPrice,
            LocalDate date,
            SettlementType type,
            List<PayInfoDto> payers
    ) {
        public Settlement toEntity(Long tripId, Long payerId, boolean isCompleted) {
            return Settlement.builder()
                    .tripId(tripId)
                    .name(name)
                    .totalPrice(totalPrice)
                    .date(date)
                    .type(type)
                    .payerId(payerId)
                    .isCompleted(isCompleted)
                    .build();
        }
    }
    
    @Builder
    public record PayInfoDto (
            Long userId,
            Long price,
            boolean isCompleted
    ) {
        public PayInfo toEntity(Long settlementId) {
            return PayInfo.builder()
                    .userId(userId)
                    .price(price)
                    .isCompleted(isCompleted)
                    .settlementId(settlementId)
                    .build();
        }
    }
}
