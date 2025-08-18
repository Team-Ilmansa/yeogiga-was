package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * TripDayPlace 내 이미지들을 재정렬하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class TripPlaceImageReassignmentService {
    private final TripDayPlaceService tripDayPlaceService;
    private final TripPlaceImageAssignmentService tripPlaceImageAssignmentService;

    /**
     * 주어진 TripDayPlace ID에 대해 이미지 재정렬을 수행하는 메서드
     * - 기존 이미지들을 모두 수집한 후 삭제
     * - 수집된 이미지를 기반으로 다시 GPS 기준으로 매핑
     *
     * @param tripDayPlaceId 재정렬 대상 TripDayPlace의 ID
     */
    public void reassignImagesToTripDayPlace(String tripDayPlaceId) {
        TripDayPlace tripDayPlace = tripDayPlaceService.readById(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_PLACE_NOT_FOUND));

        List<Image> images = new ArrayList<>();

        // 모든 Place의 이미지 수집 후 초기화
        for (Place place : tripDayPlace.getPlaces()) {
            images.addAll(place.getImages());
            place.clearImages();
        }

        // 매칭되지 않은 이미지 수집 및 초기화
        images.addAll(tripDayPlace.getUnmatchedImages());
        tripDayPlace.clearUnmatchedImages();

        tripPlaceImageAssignmentService.assignImageToTripDayPlace(tripDayPlace, images);
    }
}
