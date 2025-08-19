package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripDaySummaryRes;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPlaceQueryServiceLegacy {
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 특정 여행(tripId)에 포함된 모든 여행 일차(TripDayPlace)에 대한 요약 정보 조회 메서드
     * - 각 일차의 id, day, 포함된 place 요약 리스트를 반환
     *
     * @param tripId 조회할 여행 ID
     * @return TripDayPlace 요약 정보 리스트
     */
    public List<TripPlaceRes.TripDayPlaceInfo> getTripDayPlacesInfo(Long tripId) {
        List<TripDayPlace> tripDayPlaces = tripDayPlaceService.readByTripIdSortedByOrder(tripId);

        return tripDayPlaces.stream()
                .map(TripPlaceRes.TripDayPlaceInfo::from)
                .toList();
    }

    /**
     * 특정 여행 일차(TripDayPlace)에 포함된 모든 목적지의 상세 정보 조회 메서드
     * - 각 place의 좌표, 타입, 순서등을 포함한 상세 정보를 반환
     *
     * @param tripDayPlaceId 조회할 여행 일차 ID
     * @return 목적지 상세 정보 리스트
     */
    public List<TripPlaceRes.PlaceDetails> getPlaceDetailsInfo(String tripDayPlaceId) {
        TripDayPlace tripDayPlace = tripDayPlaceService.readByIdSortedByOrder(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_PLACE_NOT_FOUND));

        return tripDayPlace.getPlaces().stream()
                .map(TripPlaceRes.PlaceDetails::from)
                .toList();
    }

    /**
     * 여행 ID로 해당 여행의 일차별 요약 정보 조회 메서드
     * - 각 여행 일차(Day)마다 포함된 장소(Place)와 이미지(Image) 정보를 포함. (이미지는 1장. 썸네일용)
     *
     * @param tripId 조회할 여행 ID
     * @return 일차 요약 정보 리스트
     */
    public List<TripDaySummaryRes.DayDto> getTripDaySummaries(Long tripId) {
        List<TripDayPlace> tripDayPlaces = tripDayPlaceService.readTripDayPlaceSummariesByTripId(tripId);

        return tripDayPlaces.stream()
                .map(TripDaySummaryRes.DayDto::from)
                .toList();
    }
}
