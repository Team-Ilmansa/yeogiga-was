package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceReqLegacy;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Place;
import kr.co.yeogiga.domain.trip.entity.TripDay;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.PlaceService;
import kr.co.yeogiga.domain.trip.service.TripDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripPlaceCommandService {
    private final TripDayService tripDayService;
    private final PlaceService placeService;

    /**
     * 여행 일차에 새로운 목적지를 삽입 메서드
     * 삽입 위치는 현재 목적지 order 중 최대값을 기준으로 order를 계산하여 결정
     *
     * @param tripId        여행 ID
     * @param day           여행 일차
     * @param insertRequest 삽입할 장소 정보 및 위치 기준 정보
     */
    public void addNewPlace(Long tripId, int day, TripPlaceReq.Request insertRequest) {
        TripDay tripDay = tripDayService.readByTripIdAndDay(tripId, day)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_DAY_NOT_FOUND));

        int placeCount = placeService.countByTripDayId(tripDay.getId());

        placeService.save(createPlace(tripDay, insertRequest, placeCount));
    }

    /**
     * 목적지 객체를 만드는 메서드
     * 현재 목적지 order 최대값 기반으로 새로운 order를 계산하여 새로운 Place 객체를 생성
     * 1. maxPlaceOrder : 0 => 존재하는 목적지가 없는 상황
     * 2. maxPlaceOrder : 0 x => 마지막 목적지 뒤에 추가하는 상화
     *
     * @param tripDay       여행 일차 객체
     * @param request       사용자 요청 정보
     * @param maxPlaceOrder 현재 목적지 order 중 최대값 (nullable)
     * @return 생성된 Place 객체
     */
    private Place createPlace(TripDay tripDay, TripPlaceReq.Request request, int maxPlaceOrder) {
        return request.toEntity(tripDay, maxPlaceOrder + 1);
    }
}
