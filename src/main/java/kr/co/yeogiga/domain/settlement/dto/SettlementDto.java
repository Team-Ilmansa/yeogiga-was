package kr.co.yeogiga.domain.settlement.dto;

import kr.co.yeogiga.domain.settlement.type.SettlementType;

import java.time.LocalDate;
import java.util.List;

public record SettlementDto (
        Long id,
        String name,
        Long totalPrice,
        LocalDate date,
        SettlementType type,
        Long payerId,
        boolean isCompleted,
        List<PayInfoDto> payers
) {
}
