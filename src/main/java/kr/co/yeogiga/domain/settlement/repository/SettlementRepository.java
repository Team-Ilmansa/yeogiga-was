package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, CustomSettlementRepository {
}
