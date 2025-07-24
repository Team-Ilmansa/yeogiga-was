package kr.co.yeogiga.domain.notice.repository;

import kr.co.yeogiga.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
