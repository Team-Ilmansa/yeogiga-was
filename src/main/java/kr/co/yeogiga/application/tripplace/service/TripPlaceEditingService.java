package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 여행 생성 단계에서 사용자들이 선택한 "목적지"들을 임시 저장하고 관리하는 서비스 클래스.
 * - 아직 확정되지 않은 여행 목적지 편집(추가, 삭제, 수정, 조회) 수행
 * - Redis를 임시 저장소로 사용
 */
@Service
@RequiredArgsConstructor
public class TripPlaceEditingService {
    private final RedisRepository redisRepository;

    /**
     * 편집 중인 여행 일정에 새로운 목적지를 추가하는 메서드
     * - Set을 통해 이미 추가된 장소인지 확인
     * - 중복이 없다면 List & Set에 추가 (저장 포맷에 맞게 변환 후 저장)
     *
     * @param tripId : 여행 ID
     * @param day    : 여행 일차
     * @param place  : 추가할 TripPlaceReq.Request 객체
     * @throws CustomException - ALREADY_ADDED_PLACE : 이미 목적지를 추가한 경우
     */
    public void assignPlaceToDay(Long tripId, int day, TripPlaceReq.Request place) {
        String listKey = PlaceConstant.dayPlacesKey(tripId, day);
        String setKey = PlaceConstant.dayPlaceSetKey(tripId, day);

        String placeUniqueKey = makeUniqueKey(place.name(), place.latitude(), place.longitude());
        if (redisRepository.existsInSet(setKey, placeUniqueKey)) {
            throw new CustomException(TripErrorType.ALREADY_ADDED_PLACE);
        }

        redisRepository.setList(listKey, place.toStoredFormat());
        redisRepository.addToSet(setKey, placeUniqueKey);
    }

    /**
     * 특정 여행(tripId)과 일차(day)에 저장된 목적지 리스트를 조회하는 메서드
     *
     * @param tripId : 여행 ID
     * @param day    : 여행 일차 (1일차, 2일차 등)
     * @return : 저장된 TripPlaceDto.StoredFormat 리스트
     */
    public List<TripPlaceReq.StoredFormat> getAssignedPlaces(Long tripId, int day) {
        String dayPlacesKey = PlaceConstant.dayPlacesKey(tripId, day);
        return redisRepository.getList(dayPlacesKey, TripPlaceReq.StoredFormat.class);
    }

    /**
     * 편집 중인 여행 일정에서 특정 목적지를 삭제하는 메서드
     * - List에서 id에 해당하는 목적지 조회
     * - 찾은 경우 List & Set에 삭제
     *
     * @param tripId  : 여행 ID
     * @param day     : 여행 일차
     * @param placeId : 삭제할 목적지 ID
     */
    public void deleteAssignedPlace(Long tripId, int day, String placeId) {
        String dayPlacesKey = PlaceConstant.dayPlacesKey(tripId, day);
        String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);

        TripPlaceReq.StoredFormat target = findPlaceInList(dayPlacesKey, placeId);
        if (target == null) {
            return;
        }

        redisRepository.removeFromList(dayPlacesKey, target);

        String placeUniqueKey = makeUniqueKey(target.name(), target.latitude(), target.longitude());
        redisRepository.removeFromSet(dayPlaceSetKey, placeUniqueKey);
    }

    /**
     * Redis 리스트에서 주어진 placeId에 해당하는 장소를 찾아 반환하는 메서드
     *
     * @param listKey : Redis에 저장된 장소 리스트의 키
     * @param placeId : 조회할 장소의 ID
     * @return : 일치하는 장소가 존재하면 해당 객체, 없으면 null 반환
     */
    private TripPlaceReq.StoredFormat findPlaceInList(String listKey, String placeId) {
        List<TripPlaceReq.StoredFormat> places = redisRepository.getList(listKey, TripPlaceReq.StoredFormat.class);

        return places.stream()
                .filter(p -> placeId.equals(p.id()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 편집 중인 여행 일정의 목적지 순서를 수정하는 메서드
     * - 기존 List & Set 모두 삭제
     * - 새로운 순서대로 List & Set에 저장
     *
     * @param tripId         여행 ID
     * @param day            여행 일차
     * @param reorderRequest 재정렬할 목적지 ID 리스트
     */
    public void reorderPlaces(Long tripId, int day, TripPlaceReq.ReorderRequest reorderRequest) {
        String dayPlacesKey = PlaceConstant.dayPlacesKey(tripId, day);
        String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);

        List<TripPlaceReq.StoredFormat> storedPlaces =
                redisRepository.getList(dayPlacesKey, TripPlaceReq.StoredFormat.class);

        if (storedPlaces == null || storedPlaces.isEmpty()) {
            return;
        }

        Map<String, TripPlaceReq.StoredFormat> placeMap = storedPlaces.stream()
                .collect(Collectors.toMap(TripPlaceReq.StoredFormat::id, Function.identity()));

        // 요청한 정렬 순서에 맞게 목적지 재정렬
        List<TripPlaceReq.StoredFormat> reordered =
                buildReorderedPlaces(reorderRequest.orderedPlaceIds(), placeMap);

        redisRepository.del(dayPlacesKey);
        redisRepository.del(dayPlaceSetKey);

        redisRepository.setListAll(dayPlacesKey, reordered);
        for (TripPlaceReq.StoredFormat place : reordered) {
            String placeUniqueKey = makeUniqueKey(place.name(), place.latitude(), place.longitude());
            redisRepository.addToSet(dayPlaceSetKey, placeUniqueKey);
        }
    }

    /**
     * 목적지를 정렬하여 반환하는 메서드
     *
     * @param orderedIds 정렬된 Place ID 목록
     * @param placeMap   ID 기준 Place 매핑 정보
     * @return 재정렬된 Place 리스트
     */
    private List<TripPlaceReq.StoredFormat> buildReorderedPlaces(
            List<String> orderedIds,
            Map<String, TripPlaceReq.StoredFormat> placeMap
    ) {
        List<TripPlaceReq.StoredFormat> reordered = new ArrayList<>();

        for (String placeId : orderedIds) {
            TripPlaceReq.StoredFormat place = placeMap.get(placeId);
            reordered.add(place);
        }

        return reordered;
    }

    /**
     * 목적지의 고유 식별 키를 생성하는 메서드 (장소명 + 위도 + 경도 조합)
     *
     * @param name      : 목적지 이름
     * @param latitude  : 목적지 위도
     * @param longitude : 목적지 경도
     * @return : unique key
     */
    private String makeUniqueKey(String name, double latitude, double longitude) {
        return name + ":" + latitude + ":" + longitude;
    }
}
