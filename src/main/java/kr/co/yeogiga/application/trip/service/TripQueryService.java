package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripRes;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripQueryService {
    private final TripMemberService tripMemberService;
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 메인 화면 내 여행 정보를 조회하는 메서드
     *
     * @param userId        사용자 ID
     * @return              여행 정보
     *                      - 여행 전: 남은 여행 중 가장 일정이 가까운 여행 정보와 1일차 목적지 정보
     *                      - 여행 중: 진행 중인 여행과 그 일차의 목적지 정보
     *                      - 그 외: null
     */
    @Transactional
    public TripRes.TripMainInfo getTripMainInfo(Long userId) {
        List<Trip> tripList = tripMemberService.readAllTripByUserId(userId);

        Map<TravelStatus, List<Trip>> tripMap = distributeTrip(tripList);

        // 현재 진행 중인 여행이 존재 => 현재 일차 정보를 반환
        if (tripMap.get(TravelStatus.IN_PROGRESS) != null) {
            Trip trip = resolveTripByStatus(tripMap, TravelStatus.IN_PROGRESS);
            int day = calculateCurrentTripDay(trip.getStartedAt());

            return TripRes.TripMainInfo.from(trip, day, getPlaceSummaries(trip.getId(), day));
        }

        // 계획 중인 여행이 존재 => 여행 1일차 정보를 반환
        if (tripMap.get(TravelStatus.PLANNED) != null) {
            Trip trip = resolveTripByStatus(tripMap, TravelStatus.PLANNED);

            return TripRes.TripMainInfo.from(trip, 1, getPlaceSummaries(trip.getId(), 1));
        }

        // 그 외 경우에는 null 리턴 => 종료된 여행은 응답 X
        return null;
    }

    /**
     * 여행 목록을 상태(TravelStatus)에 따라 분류하는 메서드
     * - 여행 전, 중 상태의 여행 목록을 분류
     * - 종료된 여행은 포함 X
     *
     * @param tripList      여행 목록
     * @return              여행 Map - 여행 상태, 해당 여행 상태의 여행 목록
     */
    private Map<TravelStatus, List<Trip>> distributeTrip(List<Trip> tripList) {
        Map<TravelStatus, List<Trip>> tripMap = new HashMap<>();

        tripList.forEach(trip -> {
            switch (trip.getTravelStatus()) {
                case PLANNED
                        -> tripMap.computeIfAbsent(TravelStatus.PLANNED, k -> new ArrayList<>()).add(trip);
                case IN_PROGRESS
                        -> tripMap.computeIfAbsent(TravelStatus.IN_PROGRESS, k -> new ArrayList<>()).add(trip);
            }
        });

        return tripMap;
    }

    /**
     * 진행 중인 여행의 현재 일차를 계산하는 메서드
     *
     * @param startedAt     여행 시작 시간
     * @return              여행 일차
     */
    private int calculateCurrentTripDay (LocalDateTime startedAt) {
        return LocalDateTime.now().getDayOfMonth() - startedAt.getDayOfMonth() + 1;
    }

    /**
     * 여행 Map에서 특정 상태의 여행 중 가장 빠른 일정의 여행을 반환하는 메서드
     * - 여행 시작 시간(staredAt) 기준 정렬, 아직 시간이 정해지지 않은 여행은 맨 마지막에 위치
     * - 정렬 후, 가장 처음 여행을 반환
     *
     * @param tripMap       여행 Map - 여행 상태, 해당 여행 상태의 여행 목록
     * @param status        여행 상태
     * @return              해당 상태의 여행 중 가장 빠른 일정의 여행
     */
    private Trip resolveTripByStatus(Map<TravelStatus, List<Trip>> tripMap, TravelStatus status) {
        return tripMap.get(status).stream()
                .min(Comparator.comparing(Trip::getStartedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);
    }

    /**
     * 특정 일차의 여행 장소 요약 정보 목록을 조회하는 메서드
     *
     * @param tripId        여행 ID
     * @param day           여행 일차
     * @return              특정 일차의 목적지 요약 정보
     *                      - 아직 여행 목적지 정보가 없는 경우 Empty List 반환
     */
    private List<TripPlaceRes.PlaceSummary> getPlaceSummaries(Long tripId, int day) {
        List<Place> places = tripDayPlaceService.readTripDayPlaceByTripIdAndDay(tripId, day);

        return places.stream()
                .map(TripPlaceRes.PlaceSummary::from).toList();
    }

    /**
     * 사용자가 속한 여행 목록을 반환하는 메서드
     * - 여행 시작 시간(staredAt) 기준 정렬, 아직 시간이 정해지지 않은 여행 맨 뒤에 위치
     *
     * @param userId        사용자 ID(pk)
     * @return              사용자가 속한 여행 목록
     */
    @Transactional(readOnly = true)
    public List<TripRes.TripSummary> getAllTrip(Long userId) {
        return tripMemberService.readAllTripByUserId(userId).stream()
                .map(trip -> {
                    List<User> members = tripMemberService.readAllUserByTripId(trip.getId());
                    return TripRes.TripSummary.from(trip, members);
                })
                .collect(Collectors.toList());
    }
}
