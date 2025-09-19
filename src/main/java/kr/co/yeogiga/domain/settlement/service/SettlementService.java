package kr.co.yeogiga.domain.settlement.service;

import kr.co.yeogiga.domain.settlement.dto.SettlementDto;
import kr.co.yeogiga.domain.settlement.entity.Settlement;
import kr.co.yeogiga.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    
    public Long save(Settlement settlement) {
        return settlementRepository.save(settlement).getId();
    }
    
    public Optional<SettlementDto> readSettlementDtoById(Long id) {
        return settlementRepository.findSettlementDtoById(id);
    }
    
    public List<SettlementDto> readAllSettlementDtoByTripId(Long tripId) {
        return settlementRepository.findAllSettlementDtoByTripId(tripId);
    }
}
