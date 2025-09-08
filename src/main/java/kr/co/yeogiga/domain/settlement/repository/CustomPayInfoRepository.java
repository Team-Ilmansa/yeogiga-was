package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.entity.PayInfo;

import java.util.List;

public interface CustomPayInfoRepository {
    void saveAllInBatch(List<PayInfo> payInfos);
}
