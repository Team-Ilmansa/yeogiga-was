package kr.co.yeogiga.domain.notice.repository;

import kr.co.yeogiga.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long>, CustomNoticeRepository {
    
    @Modifying
    @Query("DELETE FROM notice n WHERE n.id = :id")
    void deleteById(Long id);
}
