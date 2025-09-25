package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayInfoRepository extends JpaRepository<PayInfo, Long>, CustomPayInfoRepository {
    @Modifying
    @Query(value = "DELETE FROM pay_info WHERE settlement_id = :settlementId", nativeQuery = true)
    void deleteBySettlementId(@Param(value = "settlementId") Long settlementId);
}
