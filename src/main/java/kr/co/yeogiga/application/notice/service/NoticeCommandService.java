package kr.co.yeogiga.application.notice.service;

import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.notice.entity.Notice;
import kr.co.yeogiga.domain.notice.exception.NoticeErrorType;
import kr.co.yeogiga.domain.notice.service.NoticeService;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeCommandService {
    private final NoticeService noticeService;
    private final TripService tripService;

    /**
     * 여행 공지사항을 생성하는 메서드
     *
     * @param userId 작성자 ID
     * @param tripId 여행 ID
     * @param dto    공지사항 요청 DTO
     */
    @Transactional
    public void createNotice(Long userId, Long tripId, NoticeReq.Creation dto) {
        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        if (!trip.isLeader(userId)) {
            throw new CustomException(TripErrorType.PERMISSION_DENIED_NOT_LEADER);
        }

        Notice notice = Notice.builder()
                .title(dto.title())
                .description(dto.description())
                .author(User.builder().id(userId).build())
                .tripId(tripId)
                .completed(false)
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
        Notice notice = noticeService.readById(noticeId)
                .orElseThrow(() -> new CustomException(NoticeErrorType.NOT_FOUND));
        
        if (!notice.isAuthor(userId)) {
            throw new CustomException(NoticeErrorType.UNAUTHORIZED_AUTHOR);
        }
        
        notice.update(dto.title(), dto.description());
    }

    /**
     * 공지사항 완료 여부를 수정하는 메서드
     *
     * @param noticeId 공지사항 ID
     * @param userId   수정 요청 사용자 ID
     * @param dto      완료 여부 (true: 완료, false: 미완료)
     * @throws CustomException NoticeErrorType.NOT_FOUND - 공지사항이 존재하지 않는 경우
     * @throws CustomException NoticeErrorType.UNAUTHORIZED_AUTHOR - 작성자가 아닌 사용자가 수정하려는 경우
     */
    @Transactional
    public void updateCompleted(Long noticeId, Long userId, NoticeReq.UpdateCompleted dto) {
        Notice notice = noticeService.readById(noticeId)
                .orElseThrow(() -> new CustomException(NoticeErrorType.NOT_FOUND));

        if (!notice.isAuthor(userId)) {
            throw new CustomException(NoticeErrorType.UNAUTHORIZED_AUTHOR);
        }

        notice.changeCompleted(dto.completed());
    }

    /**
     * 여행 공지사항을 삭제하는 메서드
     *
     * @param noticeId  공지사항 ID
     * @param userId    사용자 ID
     *
     * @throws CustomException  NoticeErrorType.UNAUTHORIZED_AUTHOR - 공지사항 작성자가 아닌 경우
     */
    @Transactional
    public void deleteNotice(Long noticeId, Long userId) {
        Notice notice = noticeService.readById(noticeId)
                .orElseThrow(() -> new CustomException(NoticeErrorType.NOT_FOUND));
        
        if (!notice.isAuthor(userId)) {
            throw new CustomException(NoticeErrorType.UNAUTHORIZED_AUTHOR);
        }
        
        noticeService.delete(notice);
    }
}
