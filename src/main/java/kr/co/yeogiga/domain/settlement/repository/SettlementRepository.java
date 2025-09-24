package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementRepository extends JpaRepository<Settlement, Long>, CustomSettlementRepository {
    @Modifying
    @Query(value = "DELETE FROM settlement WHERE id = :id", nativeQuery = true)
    void deleteById(@Param(value = "id") Long id);
}
