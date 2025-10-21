package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.dto.SettlementDto;
import kr.co.yeogiga.domain.settlement.entity.Settlement;

import java.util.List;
import java.util.Optional;

public interface CustomSettlementRepository {
    Optional<Settlement> findByIdJoinFetch(Long id);
    Optional<Long> findPayerIdById(Long id);
    Optional<SettlementDto> findSettlementDtoById(Long id);
    List<SettlementDto> findAllSettlementDtoByTripId(Long tripId);
}
