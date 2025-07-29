package kr.co.yeogiga.domain.notice.repository;

import kr.co.yeogiga.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository {
    Optional<Long> findAuthorIdById(Long id);
}
