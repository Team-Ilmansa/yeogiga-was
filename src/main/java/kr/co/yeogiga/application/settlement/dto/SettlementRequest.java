package kr.co.yeogiga.application.settlement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
            @NotBlank(message = "이름은 필수 입력값입니다.")
            String name,
            
            @NotNull(message = "금액은 필수 입력값입니다.")
            @Min(value = 0, message = "최소 금액은 0원입니다.")
            Long totalPrice,
            
            @NotNull(message = "날짜는 필수 입력값입니다.")
            LocalDate date,
            
            @NotNull(message = "타입은 필수 입력값입니다.")
            SettlementType type,
            
            @Valid
            @NotNull(message = "정산 인원은 필수 입력값입니다.")
            @NotEmpty(message = "정산 인원은 최소 1명 이상이어야합니다.")
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
            @NotNull(message = "사용자 id는 필수 입력값입니다.")
            Long userId,
            
            @NotNull(message = "인원 별 정산 금액은 필수 입력값입니다.")
            @Min(value = 0, message = "최소 금액은 0원입니다.")
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
