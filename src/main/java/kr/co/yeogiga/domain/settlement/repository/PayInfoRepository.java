package kr.co.yeogiga.domain.settlement.repository;

import kr.co.yeogiga.domain.settlement.entity.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayInfoRepository extends JpaRepository<PayInfo, Long>, CustomPayInfoRepository {
}
