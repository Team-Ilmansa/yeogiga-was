package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripRes;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripQueryService {
    private final TripMemberService tripMemberService;

    /**
     * 사용자가 속한 여행 목록을 반환하는 메서드
     * - 여행 시작 시간(staredAt) 기준 정렬, 아직 시간이 정해지지 않은 여행 맨 뒤에 위치
     *
     * @param userId        사용자 ID(pk)
     * @return              사용자가 속한 여행 목록
     */
    public List<TripRes.TripSummary> getAllTrip(Long userId) {
        return tripMemberService.readAllByUserId(userId).stream()
                .map(TripMember::getTrip)
                .sorted(Comparator.comparing(Trip::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(TripRes.TripSummary::from)
                .collect(Collectors.toList());
    }
}
