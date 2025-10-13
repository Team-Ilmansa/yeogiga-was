package kr.co.yeogiga.domain.settlement.service;

import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import kr.co.yeogiga.domain.settlement.repository.PayInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayInfoService {
    private final PayInfoRepository payInfoRepository;
    
    public List<PayInfo> readAllBySettlementId(Long settlementId) {
        return payInfoRepository.findAllBySettlementId(settlementId);
    }
    
    public void saveAllInBatch(List<PayInfo> payInfos) {
        payInfoRepository.saveAllInBatch(payInfos);
    }
    
    public void deleteBySettlementId(Long settlementId) {
        payInfoRepository.deleteBySettlementId(settlementId);
    }
    
    public void deleteByIds(List<Long> ids) {
        payInfoRepository.deleteByIds(ids);
    }
}
