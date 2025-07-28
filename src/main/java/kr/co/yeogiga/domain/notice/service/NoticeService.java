package kr.co.yeogiga.domain.notice.service;

import kr.co.yeogiga.domain.notice.dto.NoticeDto;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    
    public void save(Notice notice) {
        noticeRepository.save(notice);
    }
    
    public Optional<Notice> readById(Long id) {
        return noticeRepository.findById(id);
    }
    
    public Page<NoticeDto.Detail> readAllNoticeDetailByTripId(Long tripId, Pageable pageable) {
        return noticeRepository.findAllNoticeDetailByTripId(tripId, pageable);
    }
}
