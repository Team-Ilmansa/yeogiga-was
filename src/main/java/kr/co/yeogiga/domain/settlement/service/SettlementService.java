package kr.co.yeogiga.domain.settlement.service;

import kr.co.yeogiga.domain.settlement.entity.Settlement;
import kr.co.yeogiga.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;
    
    public Long save(Settlement settlement) {
        return settlementRepository.save(settlement).getId();
    }
}
