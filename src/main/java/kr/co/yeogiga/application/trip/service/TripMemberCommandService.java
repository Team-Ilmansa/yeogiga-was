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
}
