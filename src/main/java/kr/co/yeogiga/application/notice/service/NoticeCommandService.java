package kr.co.yeogiga.application.notice.service;

import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.service.NoticeService;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeCommandService {
    private final NoticeService noticeService;
    
    /**
     * 여행 공지사항을 생성하는 메서드
     *
     * @param userId    작성자 ID
     * @param tripId    여행 ID
     * @param dto       공지사항 요청 DTO
     */
    public void createNotice(Long userId, Long tripId, NoticeReq.Creation dto) {
        Notice notice = Notice.builder()
                .title(dto.title())
                .description(dto.description())
                .author(User.builder().id(userId).build())
                .tripId(tripId)
                .build();
        
        noticeService.save(notice);
    }
}
