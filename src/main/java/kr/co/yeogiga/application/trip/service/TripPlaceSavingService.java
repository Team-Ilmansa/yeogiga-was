package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripPlaceReq;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPlaceSavingService {
    private final TripDayPlaceService tripDayPlaceService;
    private final RedisRepository redisRepository;

    /**
     * 여행 생성 완료 시, Redis에 임시 저장된 일차별 목적지들을 MongoDB에 저장하는 메서드
     * - 1일차부터 lastDay까지 순회하며 TripDayPlace 생성
     * - Redis에 임시 저장한 데이터 삭제 (list, set)
     * - 생성된 TripDayPlace 리스트를 MongoDB에 일괄 저장
     *
     * @param tripId  : 여행 ID
     * @param lastDay : 여행 마지막 일차
     */
    public void completeTrip(Long tripId, int lastDay) {
        List<TripDayPlace> tripDayPlaces = new ArrayList<>();

        for (int day = 1; day <= lastDay; day++) {
            tripDayPlaces.add(createTripDayPlace(tripId, day));

            // Redis에 임시 저장된 데이터 삭제
            redisRepository.del(PlaceConstant.dayPlacesKey(tripId, day));
            redisRepository.del(PlaceConstant.dayPlaceSetKey(tripId, day));
        }

        tripDayPlaceService.saveAll(tripDayPlaces);
    }

    /**
     * 특정 일자(day)에 해당하는 TripDayPlace 객체 생성
     *
     * @param tripId : 여행 ID
     * @param day    : 일차 (1일차, 2일차 ...)
     * @return : 생성된 TripDayPlace
     */
    private TripDayPlace createTripDayPlace(Long tripId, int day) {
        String dayPlacesKey = PlaceConstant.dayPlacesKey(tripId, day);
        List<TripPlaceReq.StoredFormat> storedPlaces
                = redisRepository.getList(dayPlacesKey, TripPlaceReq.StoredFormat.class);

        List<Place> places = new ArrayList<>();
        for (int i = 0; i < storedPlaces.size(); i++) {
            TripPlaceReq.StoredFormat stored = storedPlaces.get(i);
            places.add(convertToPlace(stored, i));
        }

        return TripDayPlace.builder()
                .tripId(tripId)
                .day(day)
                .places(places)
                .build();
    }

    /**
     * StoredFormat -> Place 변환 (순서(order) 반영)
     * - 순서(order)는 10씩 증가. (효율적인 목적지 순서 번경을 위함)
     *
     * @param stored : 저장된 StoredFormat
     * @param index  : 리스트 내 순서
     * @return : Place 변환 객체
     */
    private Place convertToPlace(TripPlaceReq.StoredFormat stored, int index) {
        return Place.builder()
                .id(stored.id())
                .name(stored.name())
                .latitude(stored.latitude())
                .longitude(stored.longitude())
                .placeType(stored.placeCategory())
                .order((index + 1) * 10.0)
                .build();
    }
}
