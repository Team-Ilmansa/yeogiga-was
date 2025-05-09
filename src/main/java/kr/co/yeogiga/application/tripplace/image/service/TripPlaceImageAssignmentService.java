package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TripDayPlace 내 목적지에 이미지를 매핑하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class TripPlaceImageAssignmentService {
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 이미지 메타데이터를 불러와 해당 TripDayPlace에 할당하는 메서드
     * - 위도/경도가 있을 경우 가장 가까운 목적지(Place)에 연결
     * - 위도/경도가 없거나 목적지가 없을 경우 기타(unmatchedImages)로 분류
     *
     * @param tripDayPlaceId TripDayPlace의 ID
     */
    public void assignImageToTripDayPlace(String tripDayPlaceId, List<Image> images) {
        TripDayPlace tripDayPlace = tripDayPlaceService.readById(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_PLACE_NOT_FOUND));

        // GPS 정보를 기준으로 가장 가까운 장소별로 이미지 그룹핑
        List<Image> unmatchedImages = new ArrayList<>();
        Map<String, List<Image>> placeImageMap = groupImagesByNearestPlace(
                tripDayPlace.getPlaces(),
                images,
                unmatchedImages
        );

        // 그룹핑된 이미지들을 실제 TripDayPlace에 할당
        assignGroupedImagesToTripDayPlace(tripDayPlace, placeImageMap, unmatchedImages);

        tripDayPlaceService.save(tripDayPlace);
    }

    /**
     * 이미지들을 가장 가까운 목적지별로 그룹핑하여 매핑하는 메서드
     * - GPS 정보가 없으면 unmatchedImages에 추가
     * - 가장 가까운 장소가 없는 경우(목적지 존재 x)에도 unmatchedImages에 추가
     *
     * @param places          이미지와 매칭할 대상 장소 리스트
     * @param sourceImages    정렬할 이미지 리스트
     * @param unmatchedImages 매칭되지 않은 이미지를 저장할 리스트
     * @return 장소 ID를 키로 하고, 해당 장소에 할당될 이미지 리스트를 값으로 가지는 Map
     */
    private Map<String, List<Image>> groupImagesByNearestPlace(
            List<Place> places,
            List<Image> sourceImages,
            List<Image> unmatchedImages
    ) {
        Map<String, List<Image>> placeImageMap = new HashMap<>();

        for (Image image : sourceImages) {
            if (!hasGpsInfo(image)) {
                unmatchedImages.add(image);
                continue;
            }

            Place nearestPlace = findNearestPlace(places, image);
            if (nearestPlace != null) {
                placeImageMap.computeIfAbsent(nearestPlace.getId(), k -> new ArrayList<>()).add(image);
            } else {
                unmatchedImages.add(image);
            }
        }

        return placeImageMap;
    }

    /**
     * 그룹핑된 이미지 데이터를 실제 TripDayPlace에 할당하는 메서드
     * - 장소별로 할당된 이미지를 해당 Place 객체에 추가
     * - 매칭되지 않은 이미지는 TripDayPlace의 unmatchedImages에 추가
     *
     * @param tripDayPlace    이미지가 할당될 대상 TripDayPlace
     * @param placeImageMap   장소 ID별로 이미지가 그룹핑된 Map
     * @param unmatchedImages 매칭되지 않은 이미지 리스트
     */
    private void assignGroupedImagesToTripDayPlace(
            TripDayPlace tripDayPlace,
            Map<String, List<Image>> placeImageMap,
            List<Image> unmatchedImages
    ) {
        for (Place place : tripDayPlace.getPlaces()) {
            List<Image> assignedImages = placeImageMap.get(place.getId());
            if (assignedImages != null && !assignedImages.isEmpty()) {
                place.addImages(assignedImages);
            }
        }

        tripDayPlace.addUnmatchedImages(unmatchedImages);
    }

    /**
     * 이미지에 위도·경도 정보가 존재하는지 확인하는 메서드
     *
     * @param image 확인할 이미지 객체
     * @return 위도와 경도가 모두 존재하면 true, 아니면 false
     */
    private boolean hasGpsInfo(Image image) {
        return image.getLatitude() != null && image.getLongitude() != null;
    }

    /**
     * 이미지와 가장 가까운 위치에 있는 목적지를 TripDayPlace 내에서 탐색하는 메서드
     * - Haversine 공식을 기반으로 계산된 거리 기준
     *
     * @param places 비교 대상이 되는 장소 리스트
     * @param image  위치 기준이 될 이미지
     * @return 가장 가까운 Place 객체, 없으면 null
     */
    private Place findNearestPlace(List<Place> places, Image image) {
        return places.stream()
                .min(Comparator.comparingDouble(place ->
                        calculateDistance(image.getLatitude(), image.getLongitude(),
                                place.getLatitude(), place.getLongitude())))
                .orElse(null);
    }

    /**
     * 두 GPS 좌표 간의 지구 표면 거리(km)를 Haversine 공식으로 계산하는 메서드
     *
     * @param lat1 첫 번째 위도
     * @param lon1 첫 번째 경도
     * @param lat2 두 번째 위도
     * @param lon2 두 번째 경도
     * @return 두 지점 간 거리 (단위: km)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
