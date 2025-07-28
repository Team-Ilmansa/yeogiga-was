package kr.co.yeogiga.application.notice.service;

import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.exception.NoticeErrorType;
import kr.co.yeogiga.domain.notice.service.NoticeService;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    /**
     * 여행 공지사항을 수정하는 메서드
     *
     * @param noticeId  공지사항 ID
     * @param userId    사용자 ID
     * @param dto       공자사항 요청 DTO
     *
     * @throws CustomException  NoticeErrorType.UNAUTHORIZED_AUTHOR - 공지사항 작성자가 아닌 경우
     */
    @Transactional
    public void updateNotice(Long noticeId, Long userId, NoticeReq.Creation dto) {
        Notice notice = noticeService.readByIdJoinUser(noticeId)
                .orElseThrow(() -> new CustomException(NoticeErrorType.NOT_FOUND));
        
        if (!userId.equals(notice.getAuthor().getId())) {
            throw new CustomException(NoticeErrorType.UNAUTHORIZED_AUTHOR);
        }
        
        notice.update(dto.title(), dto.description());
    }
}
