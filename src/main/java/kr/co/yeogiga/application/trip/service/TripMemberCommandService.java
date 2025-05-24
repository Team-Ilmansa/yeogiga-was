package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripMemberCommandService {
    private final TripMemberService tripMemberService;
    private final TripService tripService;
    private final UserService userService;

    /**
     * 여행 멤버에 사용자를 등록(참가)하는 메서드
     *
     * @param tripId    여행 ID
     * @param userId    사용자 ID
     *
     * @throws CustomException  TripErrorType.TRIP_NOT_FOUND - 존재하지 않는 여행
     * @throws CustomException  UserErrorType.NOT_FOUND - 존재하지 않는 사용자
     * @throws CustomException  TripMemberErrorType.ALREADY_EXISTS - 이미 여행에 참가 중인 사용자
     */
    @Transactional
    public void joinTrip(Long tripId, Long userId) {
        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        if (tripMemberService.existsByTripIdAndUserId(tripId, userId)) {
            throw new CustomException(TripMemberErrorType.ALREADY_EXISTS);
        }

        TripMember tripMember = TripMember.builder()
                .trip(trip)
                .user(user)
                .build();

        tripMemberService.save(tripMember);
    }

    /**
     * 여행 탈퇴 메서드
     * - 여행 멤버가 2명 이상인 경우, 방장 탈퇴 불가능
     * - 탈퇴 후 여행 멤버가 존재하지 않을 경우 여행 삭제
     *
     * @param tripId            여행 ID
     * @param userId            요청 사용자 ID
     * @throws CustomException  TripErrorType.TRIP_NOT_FOUND - 존재하지 않는 여행
     * @throws CustomException  TripMemberErrorType.LEADER_CAN_NOT_LEAVE_TRIP
     *                          - 여행 멤버가 2명 이상이고, 방장이 탈퇴하려는 경우
     */
    @Transactional
    public void leaveTrip(Long tripId, Long userId) {
        List<Long> memberIds = tripMemberService.readAllUserIdByTripId(tripId);
        int memberCount = memberIds.size();

        if (!isMember(memberIds, userId)) {
            throw new CustomException(TripMemberErrorType.IS_NOT_MEMBER);
        }

        Long leaderId = tripService.readLeaderIdByTripId(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        if (memberCount != 1 && userId.equals(leaderId) ) {
            throw new CustomException(TripMemberErrorType.LEADER_CAN_NOT_LEAVE_TRIP);
        }

        tripMemberService.deleteByTripIdAndUserId(tripId, userId);

        if (memberCount == 1) {
            tripService.deleteById(tripId);
        }
    }

    /**
     * 여행 멤버 여부를 확인하는 메서드
     *
     * @param memberIds     여행 멤버 ID 목록
     * @param userId        요청 보낸 사용자 ID
     * @return              여행 멤버 여부
     */
    private boolean isMember(List<Long> memberIds, Long userId) {
        return memberIds.stream()
                .anyMatch(memberId -> memberId.equals(userId));
    }

    /**
     * 여행 멤버 추방 메서드
     *
     * @param tripId            여행 ID
     * @param userId            추방 요청 사용자 ID
     * @param targetUserId      추방 대상 사용자 ID
     *
     * @throws CustomException  TripErrorType.TRIP_NOT_FOUND - 여행 미존재
     * @throws CustomException  TripMemberErrorType.ONLY_LEADER - 요청자가 여행 방장이 아닌 경우
     * @throws CustomException  TripMemberErrorType.CAN_NOT_SELF_KICK - 자기 자신을 추방하려 하는 경우
     */
    @Transactional
    public void kickMember(Long tripId, Long userId, Long targetUserId) {
        Long leaderId = tripService.readLeaderIdByTripId(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        if (!userId.equals(leaderId)) {
            throw new CustomException(TripMemberErrorType.ONLY_LEADER);
        }

        if (userId.equals(targetUserId)) {
            throw new CustomException(TripMemberErrorType.CAN_NOT_SELF_KICK);
        }

        tripMemberService.deleteByTripIdAndUserId(tripId, targetUserId);
    }
}
