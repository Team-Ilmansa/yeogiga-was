package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripPlaceReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PlaceConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 일정에 배정되기 전 임시 저장소에 장소를 추가하는 메서드
     *
     * @param tripId : 여행 ID
     * @param place  : 추가할 TripPlaceDto.Request 객체
     */
    public void addTempPlace(Long tripId, TripPlaceReq.Request place) {
        String tempListKey = PlaceConstant.tempListKey(tripId);

        redisRepository.setList(tempListKey, place.toStoredFormat());
    }

    /**
     * 일정에 배정되기 전 임시 저장소에 있는 장소 목록을 조회하는 메서드
     *
     * @param tripId : 여행 ID
     * @return : 임시 저장된 장소 리스트
     */
    public List<TripPlaceReq.StoredFormat> getTempPlaces(Long tripId) {
        String tempListKey = PlaceConstant.tempListKey(tripId);
        return redisRepository.getList(tempListKey, TripPlaceReq.StoredFormat.class);
    }

    /**
     * 임시 저장소에서 장소를 꺼내와 특정 일차에 배정하는 메서드
     * - 이미 해당 일차에 존재하는 경우 예외 발생
     * - 배정 후 임시 저장소에서 해당 장소는 제거됨
     *
     * @param tripId  : 여행 ID
     * @param day     : 배정할 일차
     * @param placeId : 배정할 장소의 ID (임시 저장소 기준)
     */
    public void assignPlaceToDay(Long tripId, int day, String placeId) {
        String tempListKey = PlaceConstant.tempListKey(tripId);
        String dayPlacesKey = PlaceConstant.dayPlacesKey(tripId, day);
        String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);

        TripPlaceReq.StoredFormat place = findPlaceInList(tempListKey, placeId);
        if (place == null) {
            throw new CustomException(TripErrorType.NOT_FOUND_TEMP_PLACE);
        }

        String placeUniqueKey = makeUniqueKey(place.name(), place.latitude(), place.longitude());
        if (redisRepository.existsInSet(dayPlaceSetKey, placeUniqueKey)) {
            throw new CustomException(TripErrorType.ALREADY_ADDED_PLACE);
        }

        redisRepository.setList(dayPlacesKey, place);
        redisRepository.addToSet(dayPlaceSetKey, placeUniqueKey);

        deleteTempPlace(tripId, placeId);
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
     * 일정에 배정되기 전 임시 저장소에서 장소를 삭제하는 메서드
     *
     * @param tripId  : 여행 ID
     * @param placeId : 삭제할 장소 ID
     */
    public void deleteTempPlace(Long tripId, String placeId) {
        String listKey = PlaceConstant.tempListKey(tripId);

        TripPlaceReq.StoredFormat target = findPlaceInList(listKey, placeId);
        if (target == null) {
            return;
        }

        redisRepository.removeFromList(listKey, target);
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
     * @param listKey  : Redis에 저장된 장소 리스트의 키
     * @param placeId  : 조회할 장소의 ID
     * @return         : 일치하는 장소가 존재하면 해당 객체, 없으면 null 반환
     */
    private TripPlaceReq.StoredFormat findPlaceInList(String listKey, String placeId) {
        List<TripPlaceReq.StoredFormat> places = redisRepository.getList(listKey, TripPlaceReq.StoredFormat.class);

        return places.stream()
                .filter(p -> placeId.equals(p.id()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 편집 중인 여행 일정의 목적지를 수정하는 메서드 (목적지의 새로운 순서로 덮어쓰기 등)
     * - 기존 List & Set 모두 삭제
     * - 새로운 순서대로 List & Set에 저장
     *
     * @param tripId : 여행 ID
     * @param day    : 여행 일차
     * @param places : 새로운 순서의 TripPlaceDto.Request 리스트
     */
    public void updatePlaces(Long tripId, int day, List<TripPlaceReq.Request> places) {
        String dayPlacesKey = PlaceConstant.dayPlacesKey(tripId, day);
        String dayPlaceSetKey = PlaceConstant.dayPlaceSetKey(tripId, day);

        redisRepository.del(dayPlacesKey);
        redisRepository.del(dayPlaceSetKey);

        for (TripPlaceReq.Request place : places) {
            redisRepository.setList(dayPlacesKey, place.toStoredFormat());
            String placeUniqueKey = makeUniqueKey(place.name(), place.latitude(), place.longitude());
            redisRepository.addToSet(dayPlaceSetKey, placeUniqueKey);
        }
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
