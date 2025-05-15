package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripCommandService {
    private final TripService tripService;
    private final TripMemberService tripMemberService;
    private final UserService userService;

    @Transactional
    public void create(Long leaderId, TripReq.Creation creationRequest) {
        Trip trip = Trip.builder()
                .title(creationRequest.title())
                .city(creationRequest.city())
                .leaderId(leaderId)
                .travelStatus(TravelStatus.PLANNED)
                .build();

        User leader = userService.readById(leaderId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        TripMember tripMember = TripMember.builder()
                .trip(trip)
                .user(leader)
                .build();

        tripService.save(trip);
        tripMemberService.save(tripMember);
    }
}
