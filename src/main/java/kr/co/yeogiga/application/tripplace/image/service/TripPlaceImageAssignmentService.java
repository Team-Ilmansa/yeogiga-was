package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.common.util.DistanceUtils;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import kr.co.yeogiga.domain.triproute.entity.Route;
import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import kr.co.yeogiga.domain.triproute.service.TripRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private final TripRouteService tripRouteService;

    /**
     * 이미지 메타데이터를 불러와 해당 TripDayPlace에 할당하는 메서드
     * - 위도/경도가 있을 경우 가장 가까운 목적지(Place)에 연결
     * - 위도/경도가 없거나 목적지가 없을 경우 기타(unmatchedImages)로 분류
     *
     * @param tripDayPlace TripDayPlace 객체
     * @param images       처리하고자 하는 이미지 리스트
     */
    public void assignImageToTripDayPlace(TripDayPlace tripDayPlace, List<Image> images) {
        TripRoute tripRoute = tripRouteService.readByTripIdAndDay(
                tripDayPlace.getTripId(), tripDayPlace.getDay()
        ).orElse(null);

        List<Route> routes = (tripRoute != null) ? tripRoute.getRoutes() : Collections.emptyList();

        // GPS 정보를 기준으로 가장 가까운 장소별로 이미지 그룹핑
        List<Image> unmatchedImages = new ArrayList<>();
        Map<String, List<Image>> placeImageMap = groupImagesByNearestPlace(
                tripDayPlace.getPlaces(),
                images,
                unmatchedImages,
                routes
        );

        // 그룹핑된 이미지들을 실제 TripDayPlace에 할당
        assignGroupedImagesToTripDayPlace(tripDayPlace, placeImageMap, unmatchedImages);

        tripDayPlaceService.save(tripDayPlace);
    }

    /**
     * 이미지들을 가장 가까운 목적지별로 그룹핑하여 매핑하는 메서드
     * - GPS가 존재하지 않지만 시간 데이터가 존재한다면, 매칭할 일정에 대한 이동 경로를 통해 GPS 정보를 매핑
     * - GPS 정보가 없으면 unmatchedImages에 추가
     * - 가장 가까운 장소가 없는 경우(목적지 존재 x)에도 unmatchedImages에 추가
     *
     * @param places          이미지와 매칭할 대상 장소 리스트
     * @param sourceImages    정렬할 이미지 리스트
     * @param unmatchedImages 매칭되지 않은 이미지를 저장할 리스트
     * @param routes          매칭할 일정에 대한 이동 경로
     * @return 장소 ID를 키로 하고, 해당 장소에 할당될 이미지 리스트를 값으로 가지는 Map
     */
    private Map<String, List<Image>> groupImagesByNearestPlace(
            List<Place> places,
            List<Image> sourceImages,
            List<Image> unmatchedImages,
            List<Route> routes
    ) {
        Map<String, List<Image>> placeImageMap = new HashMap<>();

        for (Image image : sourceImages) {

            // GPS 정보는 없지만 이미지에 시간데이터는 있는 경우, 여행 루트를 통해 GPS 정보 탐색
            if (!hasGpsInfo(image) && image.getDate() != null) {
                Route nearestRoute = findNearestLocationFromRoutesByTime(routes, image.getDate());
                if (nearestRoute != null) {
                    image.updateGps(nearestRoute.getLatitude(), nearestRoute.getLongitude());
                }
            }

            // GPS가 존재하지 않는 경우, 기타(unmatchedImages)에 저장
            if (!hasGpsInfo(image)) {
                unmatchedImages.add(image);
                continue;
            }

            // 이미지의 GPS 정보를 통해 가까운 목적지를 탐색
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
     * 이미지의 촬영 시간과 가장 가까운 시간 정보를 가진 이동 경로(Route)를 찾는 메서드
     * - 이미지의 촬영 시간(imageTime)과의 시간 차이가 가장 작은 Route를 반환
     * - 일치하는 Route가 없으면 null을 반환
     *
     * @param routes     비교 대상이 되는 이동 경로(Route) 리스트
     * @param imageTime  이미지의 촬영 시간
     * @return           가장 가까운 시간의 Route 객체, 없으면 null
     */
    private Route findNearestLocationFromRoutesByTime(List<Route> routes, LocalDateTime imageTime) {
        return routes.stream()
                .filter(route -> route.getTime() != null)
                .min(Comparator.comparingLong(route ->
                        Math.abs(Duration.between(imageTime, route.getTime()).toMillis())))
                .orElse(null);
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
                        DistanceUtils.calculateDistance(image.getLatitude(), image.getLongitude(),
                                place.getLatitude(), place.getLongitude())))
                .orElse(null);
    }
}
