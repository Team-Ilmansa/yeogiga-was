package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TripCommandService {
    private final TripService tripService;
    private final TripMemberService tripMemberService;
    private final UserService userService;

    /**
     * 여행 생성 메서드
     *
     * @param leaderId          방장(여행 생성자) ID
     * @param creationRequest   여행 생성 요청 DTO
     */
    @Transactional
    public Long create(Long leaderId, TripReq.Creation creationRequest) {
        Trip trip = Trip.builder()
                .title(creationRequest.title())
                .leaderId(leaderId)
                .travelStatus(TravelStatus.SETTING)
                .build();

        User leader = userService.readById(leaderId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        TripMember tripMember = TripMember.builder()
                .trip(trip)
                .user(leader)
                .build();


        tripMemberService.save(tripMember);
        return tripService.save(trip).getId();
    }

    /**
     * 여행 시간 수정 메서드
     * - 여행 일정과 목적지까지 확정이 나지 않은 상태(SETTING)일 경우, 여행 상태(TravelStatus)값 미변경
     *
     * @param tripId        여행 ID
     * @param userId        요청자 ID
     * @param time          여행 시간 수정 요청 DTO
     *
     * @throws CustomException  TripErrorType.TRIP_NOT_FOUND - 여행 조회 불가
     * @throws CustomException  TripErrorType.INVALID_DATE_RANGE - 여행 시간 범위 오류 (종료 시각 <= 출발시각)
     * @throws CustomException  TripErrorType.PERMISSION_DENIED_NOT_LEADER - 방장이 아닌 사용자
     */
    @Transactional
    public void updateTime(Long tripId, Long userId, TripReq.Time time) {
        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        if (!time.isValid()) {
            throw new CustomException(TripErrorType.INVALID_DATE_RANGE);
        }

        if (!trip.getLeaderId().equals(userId)) {
            throw new CustomException(TripErrorType.PERMISSION_DENIED_NOT_LEADER);
        }

        if (trip.getTravelStatus() != TravelStatus.SETTING) {
            TravelStatus status = TravelStatus.resolveStatus(time.start(), time.end());
            trip.updateStatus(status);
        }

        trip.updateTime(time.start(), time.end());
    }

    /**
     * 여행 상태 갱신 메서드
     *
     * @param time      현재 시간
     */
    @Transactional
    public void updateTravelStatus(LocalDateTime time) {
        tripService.updateAllTravelStatusToInProgress(time);
        tripService.updateAllTravelStatusToCompleted(time);
    }

    /**
     * 여행 정보를 갱신하는 메서드
     * - 여행 제목 갱신
     *
     * @param tripId            여행 ID
     * @param updateRequest     여행 업데이터 요청 DTO (title)
     */
    @Transactional
    public void updateTripInfo(Long tripId, TripReq.Update updateRequest) {
        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        if (trip.getTitle().equals(updateRequest.title())) {
            throw new CustomException(TripErrorType.SAME_TRIP_TITLE);
        }

        trip.updateInfo(updateRequest.title());
    }

    /**
     * 여행 삭제 메서드
     *
     * @param tripId    여행 ID
     */
    public void removeTrip(Long tripId) {
        tripService.deleteById(tripId);
    }
}
