package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.dto.SettlementDto;

import java.util.Optional;

public interface CustomSettlementRepository {
    Optional<SettlementDto> findSettlementDtoById(Long id);
}
