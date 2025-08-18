package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Place;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripDay;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.PlaceService;
import kr.co.yeogiga.domain.trip.service.TripDayService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPlaceSavingService {
    private final TripService tripService;
    private final TripDayService tripDayService;
    private final PlaceService placeService;
    private final RedisRepository redisRepository;

    /**
     * 여행 생성 완료 시, Redis에 임시 저장된 일차별 목적지들을 MongoDB에 저장하는 메서드
     * - 1일차부터 lastDay까지 순회하며 TripDayPlace 생성
     * - Redis에 임시 저장한 데이터 삭제 (list, set)
     * - 생성된 TripDayPlace 리스트를 MongoDB에 일괄
     * - 여행 생성 완료 시, 여행 상태 변경
     *
     * @param tripId  : 여행 ID
     * @param lastDay : 여행 마지막 일차
     */
    @Transactional
    public void completeTrip(Long tripId, int lastDay) {
        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        List<Place> places = new ArrayList<>();
        for (int day = 1; day <= lastDay; day++) {
            places.addAll(createTripDayPlace(trip, day));

            // Redis에 임시 저장된 데이터 삭제
            redisRepository.del(PlaceConstant.dayPlacesKey(tripId, day));
            redisRepository.del(PlaceConstant.dayPlaceSetKey(tripId, day));
        }

        trip.updateStatus(TravelStatus.resolveStatus(trip.getStartedAt(), trip.getEndedAt()));

        placeService.saveAll(places);
    }

    /**
     * 특정 일자(day)에 해당하는 여행 일차(TripDay) 객체 생성 및 목적지(place) 리스트 반환
     *
     * @param trip : 여행 객체
     * @param day  : 일차 (1일차, 2일차 ...)
     */
    private List<Place> createTripDayPlace(Trip trip, int day) {
        String dayPlacesKey = PlaceConstant.dayPlacesKey(trip.getId(), day);
        List<TripPlaceReq.StoredFormat> storedPlaces
                = redisRepository.getList(dayPlacesKey, TripPlaceReq.StoredFormat.class);

        TripDay tripDay = tripDayService.save(TripDay.builder()
                .day(day)
                .trip(trip)
                .build());

        List<Place> places = new ArrayList<>();
        for (int index = 0; index < storedPlaces.size(); index++) {
            TripPlaceReq.StoredFormat stored = storedPlaces.get(index);
            places.add(convertToPlace(stored, tripDay, index));
        }

        return places;
    }

    /**
     * StoredFormat -> Place 변환 (순서(order) 반영)
     *
     * @param stored  : 저장된 StoredFormat
     * @param tripDay : 여행 일차 객체
     * @param index   : 리스트 내 순서
     * @return : Place 변환 객체
     */
    private Place convertToPlace(TripPlaceReq.StoredFormat stored, TripDay tripDay, int index) {
        return Place.builder()
                .name(stored.name())
                .latitude(stored.latitude())
                .longitude(stored.longitude())
                .placeType(stored.placeCategory())
                .sortOrder(index + 1)
                .tripDay(tripDay)
                .build();
    }
}
