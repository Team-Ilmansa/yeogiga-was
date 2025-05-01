package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class TripPlaceImageService {
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 주어진 이미지 메타데이터 통해 해당 TripDayPlace에 할당하는 메서드
     * - 위도/경도가 있을 경우 가장 가까운 목적지(Place)에 연결
     * - 위도/경도가 없거나 목적지가 없을 경우 기타(unmatchedImages)로 분류
     *
     * @param tripDayPlaceId TripDayPlace의 ID
     * @param image          GPS 정보와 시간 정보를 가진 이미지 객체
     */
    public void assignImageToTripDayPlace(String tripDayPlaceId, Image image) {
        TripDayPlace tripDayPlace = tripDayPlaceService.readById(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(TripErrorType.DAY_PLACE_NOT_FOUND));

        // 위도 경도 없는 경우
        if (!hasGpsInfo(image)) {
            assignToUnmatched(tripDayPlace, image);
            return;
        }

        Place nearestPlace = findNearestPlace(tripDayPlace, image);

        if (nearestPlace != null) {     // 담겨진 목적지가 없는 경우
            nearestPlace.addImage(image);
        } else {
            assignToUnmatched(tripDayPlace, image);
        }

        tripDayPlaceService.save(tripDayPlace);
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
     * GPS 정보가 없거나 적절한 목적지를 찾지 못한 이미지를 기타 목록에 추가하는 메서드
     *
     * @param tripDayPlace 이미지가 속한 TripDayPlace
     * @param image        기타로 분류할 이미지
     */
    private void assignToUnmatched(TripDayPlace tripDayPlace, Image image) {
        tripDayPlace.addUnmatchedImage(image);
        tripDayPlaceService.save(tripDayPlace);
    }

    /**
     * 이미지와 가장 가까운 위치에 있는 목적지를 TripDayPlace 내에서 탐색하는 메서드
     * - Haversine 공식을 기반으로 계산된 거리 기준
     *
     * @param tripDayPlace 비교 대상이 되는 장소 리스트
     * @param image        위치 기준이 될 이미지
     * @return 가장 가까운 Place 객체, 없으면 null
     */
    private Place findNearestPlace(TripDayPlace tripDayPlace, Image image) {
        return tripDayPlace.getPlaces().stream()
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
