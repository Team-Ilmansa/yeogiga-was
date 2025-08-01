package kr.co.yeogiga.application.notice.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.notice.dto.NoticeDto;
import kr.co.yeogiga.domain.notice.exception.NoticeErrorType;
import kr.co.yeogiga.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeQueryService {
    private final NoticeService noticeService;
    
    /**
     * 특정 여행에 모든 공지를 조회하는 메서드
     *
     * @param tripId    여행 ID
     * @param pageable  Pageable 객체
     * @return          여행 공지 정보 페이지 객체
     */
    public Page<NoticeDto.Detail> getAllNotices(Long tripId, Pageable pageable) {
        return noticeService.readAllNoticeDetailByTripId(tripId, pageable);
    }
    
    /**
     * 특정 여행을 조회하는 메서드
     *
     * @param noticeId  공지사항 ID
     * @return          공지사항 정보
     */
    public NoticeDto.Detail getNotice(Long noticeId) {
        return noticeService.readJoinUserById(noticeId)
                .map(NoticeDto.Detail::fromEntity)
                .orElseThrow(() -> new CustomException(NoticeErrorType.NOT_FOUND));
    }
}
