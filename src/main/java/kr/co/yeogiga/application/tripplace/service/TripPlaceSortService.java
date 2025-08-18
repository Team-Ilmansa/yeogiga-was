package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.common.util.DistanceUtils;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class TripPlaceSortService {
    private final RedisRepository redisRepository;

    /**
     * 여행 ID와 날짜를 기반으로 해당 일자의 장소들을 정렬하는 메서드
     * 정렬 기준 :
     * 1. 숙소는 항상 마지막에 배치
     * 2. 나머지 장소는 위도/경도 기준으로 가까운 거리 순으로 정렬
     * 3. 식당이 3개 이상 연속되지 않도록 배치
     *
     * @param tripId 여행 ID
     * @param day    일차 (1일차, 2일차 등)
     */
    public void sortDayTripPlaces(Long tripId, int day) {
        String listKey = PlaceConstant.dayPlacesKey(tripId, day);
        List<TripPlaceReq.StoredFormat> places =
                redisRepository.getList(listKey, TripPlaceReq.StoredFormat.class);

        if (places == null || places.isEmpty()) {
            return;
        }

        // 숙소 카테고리 & 나머지 카테고리 분리
        List<TripPlaceReq.StoredFormat> lodgings = new ArrayList<>();
        List<TripPlaceReq.StoredFormat> otherPlaces = new ArrayList<>();
        partitionPlaces(places, otherPlaces, lodgings);

        // 위도/경도 기준 목적지 정렬
        sortByDistance(otherPlaces);

        // 연속된 식당 카테고리 방지 (3번 이상 방지)
        List<TripPlaceReq.StoredFormat> sorted = preventConsecutiveRestaurants(otherPlaces);

        // 마지막에 숙소 삽입
        sorted.addAll(lodgings);

        redisRepository.del(listKey);
        redisRepository.setListAll(listKey, sorted);
    }

    /**
     * 숙소와 그 외 카테고리를 분리하여 각각의 리스트에 저장하는 메서드
     *
     * @param places      전체 장소 목록
     * @param otherPlaces 숙소 외의 장소를 저장할 리스트
     * @param lodgings    숙소만 저장할 리스트
     */
    private void partitionPlaces(
            List<TripPlaceReq.StoredFormat> places,
            List<TripPlaceReq.StoredFormat> otherPlaces,
            List<TripPlaceReq.StoredFormat> lodgings
    ) {
        for (TripPlaceReq.StoredFormat place : places) {
            if (PlaceCategory.LODGING == place.placeCategory()) {
                lodgings.add(place);
            } else {
                otherPlaces.add(place);
            }
        }
    }

    /**
     * 위도/경도를 통해 목적지를 정렬하는 메서드
     * - 첫번째 목적지를 기준으로 목적지를 정렬
     *
     * @param places 정렬할 장소 목록
     */
    private void sortByDistance(List<TripPlaceReq.StoredFormat> places) {
        if (places.isEmpty()) return;

        TripPlaceReq.StoredFormat basePlace = places.get(0);
        places.sort(Comparator.comparingDouble(p ->
                        DistanceUtils.calculateDistance(
                                basePlace.latitude(), basePlace.longitude(), p.latitude(), p.longitude()
                        )
                )
        );
    }

    /**
     * 식당이 3개 이상 연속되지 않도록 장소들을 재배치하는 메서드
     *
     * @param input 정렬된 장소 목록
     * @return 재배치된 장소 목록
     */
    private List<TripPlaceReq.StoredFormat> preventConsecutiveRestaurants(List<TripPlaceReq.StoredFormat> input) {
        List<TripPlaceReq.StoredFormat> result = new ArrayList<>();
        Queue<TripPlaceReq.StoredFormat> restaurants = new LinkedList<>();

        for (TripPlaceReq.StoredFormat place : input) {
            if (result.contains(place)) continue;

            if (isRestaurant(place)) {  // 식당 카테고리인 경우
                restaurants.add(place);

                if (restaurants.size() < 3) {
                    continue;
                }

                // 식당 3개 연속 방지 로직
                TripPlaceReq.StoredFormat nonRestaurant = findNextNonRestaurant(input, result, restaurants);
                if (nonRestaurant != null) {
                    result.add(restaurants.poll());    // 식당 1
                    result.add(nonRestaurant);         // 식당 이외의 카테고리
                    result.addAll(restaurants);        // 식당 2, 3
                } else {
                    result.addAll(restaurants);        // 식당 이외의 카테고리가 더 이상 없을 경우
                }

                restaurants.clear();
            } else {    // 식당 카테고리가 아닌 경우
                result.addAll(restaurants);
                restaurants.clear();
                result.add(place);
            }
        }

        // 삽입되지 않은 식당 카테고리 목적지
        result.addAll(restaurants);
        restaurants.clear();

        return result;
    }

    /**
     * 아직 결과에 포함되지 않았고 식당이 아닌 장소를 찾는 메서드
     * - 중복을 피하기 위해 담긴 목적지 및 restaurants에 포함되지 않은 항목만 반환
     *
     * @param input       전체 입력 리스트
     * @param result      현재까지 결과에 추가된 장소들
     * @param restaurants 현재 연속된 식당 리스트
     * @return 조건에 맞는 식당 이외 장소, 없으면 null
     */
    private TripPlaceReq.StoredFormat findNextNonRestaurant(
            List<TripPlaceReq.StoredFormat> input,
            List<TripPlaceReq.StoredFormat> result,
            Queue<TripPlaceReq.StoredFormat> restaurants
    ) {
        return input.stream()
                .filter(p -> !isRestaurant(p) && !result.contains(p) && !restaurants.contains(p))
                .findFirst()
                .orElse(null);
    }

    /**
     * 해당 장소가 식당인지 여부를 판단 메서드
     *
     * @param place 장소 객체
     * @return true = 식당, false = 식당 아님
     */
    private boolean isRestaurant(TripPlaceReq.StoredFormat place) {
        return PlaceCategory.RESTAURANT == place.placeCategory();
    }
}
