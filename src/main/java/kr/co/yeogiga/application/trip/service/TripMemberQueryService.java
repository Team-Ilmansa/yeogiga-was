package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripMemberRes;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripMemberQueryService {
    private final TripMemberService tripMemberService;

    /**
     * 여행 ID를 통해 모든 여행 멤버를 조회하는 메서드
     *
     * @param tripId        여행 ID
     * @return              여행 멤버 목록
     */
    public List<TripMemberRes.MemberInfo> getTripMembers(Long tripId) {
        return tripMemberService.readAllUserByTripId(tripId).stream()
                .map(TripMemberRes.MemberInfo::fromEntity)
                .toList();
    }
}
