package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripPlaceCommandService {
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 여행 일차에 새로운 목적지를 삽입 메서드
     * 삽입 위치는 prevPlaceId, nextPlaceId를 기준으로 order를 계산하여 결정
     *
     * @param tripPlaceId   여행 일차(TripDayPlace)의 ID
     * @param insertRequest 삽입할 장소 정보 및 위치 기준 정보
     */
    public void addNewPlace(String tripPlaceId, TripPlaceDto.InsertRequest insertRequest) {
        Double prevPlaceOrder = getPlaceOrder(tripPlaceId, insertRequest.prevPlaceId());
        Double nextPlaceOrder = getPlaceOrder(tripPlaceId, insertRequest.nextPlaceId());

        tripDayPlaceService.savePlace(
                tripPlaceId,
                createPlace(insertRequest, prevPlaceOrder, nextPlaceOrder)
        );
    }

    /**
     * 목적지의 순서(order)를 반환하는 메서드
     * - 주어진 placeId가 존재할 경우, 해당 place의 order 값을 반환
     *
     * @param tripPlaceId 여행 일차 ID
     * @param placeId     참조할 목적지 ID (nullable)
     * @return order 값 또는 null
     */
    private Double getPlaceOrder(String tripPlaceId, String placeId) {
        if (placeId != null) {
            return tripDayPlaceService.readOrderByIdAndPlaceId(tripPlaceId, placeId);
        }

        return null;
    }

    /**
     * 목적지 객체를 만드는 메서드
     * 삽입 기준이 되는 prev/next order 값을 기반으로 새로운 order를 계산하여 새로운 Place 객체를 생성
     * 1. prev/next 모두 null => 존재하는 목적지가 없는 상황
     * 2. prev가 null => 목적지를 가장 앞에 추가하는 상황
     * 3. next가 null => 목적지를 가장 뒤에 추가하는 상황
     * 4. prev/next 모두 null x => 목적지 사이에 추가하는 상황
     *
     * @param insertRequest  사용자 요청 정보
     * @param prevPlaceOrder 이전 목적지의 order 값 (nullable)
     * @param nextPlaceOrder 다음 목적지의 order 값 (nullable)
     * @return 생성된 Place 객체
     */
    private Place createPlace(TripPlaceDto.InsertRequest insertRequest, Double prevPlaceOrder, Double nextPlaceOrder) {
        if (prevPlaceOrder == null && nextPlaceOrder == null) {
            return insertRequest.toEntity(10.0);
        }

        if (prevPlaceOrder == null) {
            return insertRequest.toEntity(nextPlaceOrder / 2);
        }

        if (nextPlaceOrder == null) {
            return insertRequest.toEntity(prevPlaceOrder + 10.0);
        }

        return insertRequest.toEntity((prevPlaceOrder + nextPlaceOrder) / 2);
    }

    /**
     * 목적지를 정렬하는 메서드
     * 여행 일차에 등록된 모든 장소(Place)의 순서를 클라이언트가 전달한 순서대로 재정렬
     *
     * @param tripDayPlaceId 여행 일차(TripDayPlace)의 ID
     * @param reorderRequest 재정렬할 목적지 ID 리스트
     */
    public void reorderPlaces(String tripDayPlaceId, TripPlaceDto.ReorderRequest reorderRequest) {
        TripDayPlace tripDayPlace = tripDayPlaceService.readById(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(TripErrorType.DAY_PLACE_NOT_FOUND));

        Map<String, Place> placeMap = tripDayPlace.getPlaces().stream()
                .collect(Collectors.toMap(Place::getId, Function.identity()));

        List<Place> reordered = buildReorderedPlaces(reorderRequest.orderedPlaceIds(), placeMap);

        tripDayPlace.updatePlaces(reordered);
        tripDayPlaceService.save(tripDayPlace);
    }

    /**
     * 목적지를 정렬하여 반환하는 메서드
     * 정렬된 placeId 리스트를 기준으로 order 값을 10 단위로 재부여하여 Place 리스트를 생성
     *
     * @param orderedIds 정렬된 Place ID 목록
     * @param placeMap   ID 기준 Place 매핑 정보
     * @return 재정렬된 Place 리스트
     */
    private List<Place> buildReorderedPlaces(List<String> orderedIds, Map<String, Place> placeMap) {
        List<Place> reordered = new ArrayList<>();

        for (int i = 0; i < orderedIds.size(); i++) {
            String placeId = orderedIds.get(i);
            Place place = placeMap.get(placeId);

            place.updateOrder((i + 1) * 10.0);
            reordered.add(place);
        }

        return reordered;
    }

    /**
     * 여행 일차(TripDayPlace)에서 특정 목적지를 삭제하는 메서드
     *
     * @param tripPlaceId 여행 일차 ID
     * @param placeId     삭제할 목적지 ID
     */
    public void deletePlace(String tripPlaceId, String placeId) {
        tripDayPlaceService.deletePlace(tripPlaceId, placeId);
    }
}
