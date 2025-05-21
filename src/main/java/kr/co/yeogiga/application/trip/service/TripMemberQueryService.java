package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripMemberRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripMemberQueryService {
    private final TripMemberService tripMemberService;
    private final TripService tripService;

    /**
     * 여행 ID를 통해 모든 여행 멤버를 조회하는 메서드
     *
     * @param tripId        여행 ID
     * @return              여행 멤버 목록
     */
    @Transactional(readOnly = true)
    public List<TripMemberRes.MemberInfo> getTripMembers(Long tripId) {
        if (!tripService.existsById(tripId)) {
            throw new CustomException(TripErrorType.TRIP_NOT_FOUND);
        }

        return tripMemberService.readAllUserByTripId(tripId).stream()
                .map(TripMemberRes.MemberInfo::fromEntity)
                .toList();
    }
}
