package kr.co.yeogiga.application.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.yeogiga.common.validation.EnumValidation;
import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import kr.co.yeogiga.domain.settlement.entity.Settlement;
import kr.co.yeogiga.domain.settlement.type.SettlementType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class SettlementRequest {
    
    @Schema(name = "SettlementRequest.SettlementDto", description = "정산 내역 생성 요청 DTO")
    @Builder
    public record SettlementDto(
            @Schema(description = "정산 내역 이름", example = "점심 식사")
            @NotBlank(message = "이름은 필수 입력값입니다.")
            String name,
            
            @Schema(description = "금액", example = "50000")
            @NotNull(message = "금액은 필수 입력값입니다.")
            @Min(value = 0, message = "최소 금액은 0원입니다.")
            Long totalPrice,
            
            @Schema(description = "날짜", example = "2025-09-08")
            @NotNull(message = "날짜는 필수 입력값입니다.")
            LocalDate date,
            
            @Schema(description = "정산 내역 타입", examples = {"TOURISM(관광지)", "LODGING(숙소)", "RESTAURANT(식사)", "TRANSPORT(교통수단)", "ETC(기타)"})
            @EnumValidation(target = SettlementType.class, message = "지원하지 않는 정산 내역 타입입니다.")
            @NotNull(message = "타입은 필수 입력값입니다.")
            SettlementType type,
            
            @Schema(description = "정산 인원 목록")
            @Valid
            @NotNull(message = "정산 인원은 필수 입력값입니다.")
            @NotEmpty(message = "정산 인원은 최소 1명 이상이어야합니다.")
            List<PayInfoDto> payers
    ) {
        public Settlement toEntity(Long tripId, Long payerId) {
            return Settlement.builder()
                    .tripId(tripId)
                    .name(name)
                    .totalPrice(totalPrice)
                    .date(date)
                    .type(type)
                    .payerId(payerId)
                    .build();
        }
        
        @Schema(hidden = true)
        public boolean isValidPrice() {
            return this.totalPrice == payers.stream()
                    .mapToLong(PayInfoDto::price)
                    .sum();
        }
    }
    
    @Schema(name = "SettlementRequest.PayInfoDto", description = "인원 별 정산 금액 및 여부 DTO")
    @Builder
    public record PayInfoDto(
            @Schema(description = "사용자 ID", example = "1")
            @NotNull(message = "사용자 id는 필수 입력값입니다.")
            Long userId,
            
            @Schema(description = "해당 인원의 정산 금액", example = "10000")
            @NotNull(message = "인원 별 정산 금액은 필수 입력값입니다.")
            @Min(value = 0, message = "최소 금액은 0원입니다.")
            Long price
    ) {
        public PayInfo toEntity(Long settlementId) {
            return PayInfo.builder()
                    .userId(userId)
                    .price(price)
                    .settlementId(settlementId)
                    .build();
        }
    }
    
    @Schema(name = "SettlementRequest.PayInfoCompletionListDto", description = "분담 내역 완료 여부 리스트 DTO")
    public record PayInfoCompletionListDto(
        @Schema(
                description = "분담 내역 완료 여부 리스트",
                example = "[{\"payInfoId\": 1, \"isCompleted\": true}, {\"payInfoId\": 2, \"isCompleted\": false}]"
        )
        @Size(min = 1, message = "정산 완료 목록은 최소 1개 이상이어야 합니다.")
        List<PayInfoCompletionDto> payInfos
    ) { }
    
    @Schema(name = "SettlementRequest.PayInfoCompletionDto", description = "분담 내역 완료 여부 DTO")
    public record PayInfoCompletionDto(
            @Schema(description = "분담 내역 ID", examples = "1")
            @NotNull(message = "분담자 id는 필수 입력값입니다.")
            Long payInfoId,
            
            @Schema(description = "정산 완료 여부", examples = "true")
            boolean isCompleted
    ) { }
}
