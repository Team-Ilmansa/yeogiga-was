package kr.co.yeogiga.domain.notice.repository;

import kr.co.yeogiga.domain.notice.dto.NoticeDto;
import kr.co.yeogiga.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomNoticeRepository {
    Optional<Long> findAuthorIdById(Long id);
    Optional<Notice> findNoticeJoinUser(Long id);
    Page<NoticeDto.Detail> findAllNoticeDetailByTripId(Long tripId, Pageable pageable);
}
