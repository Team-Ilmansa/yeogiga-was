package kr.co.yeogiga.domain.notice.repository;

import kr.co.yeogiga.domain.notice.dto.NoticeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomNoticeRepository {
    Page<NoticeDto.Detail> findAllNoticeDetailByTripId(Long tripId, Pageable pageable);
}
