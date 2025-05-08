package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceImageDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.exception.ImageErrorType;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripPlaceImageMovementService {
    private final TripDayPlaceService tripDayPlaceService;

    /**
     * 특정 Place의 이미지를 다른 Place로 이동시키는 메서드
     *
     * @param tripDayPlaceId TripDayPlace 문서 ID
     * @param imageReq       이미지 이동에 필요한 정보를 담은 요청 DTO
     */
    public void moveImageToAnotherPlace(String tripDayPlaceId, TripPlaceImageDto.ImageMoveReq imageReq) {
        Image image = getImageFromTripPlace(tripDayPlaceId, imageReq.fromPlaceId(), imageReq.imageId());

        tripDayPlaceService.deleteImage(tripDayPlaceId, imageReq.fromPlaceId(), imageReq.imageId());
        tripDayPlaceService.saveImage(tripDayPlaceId, imageReq.toPlaceId(), image);
    }

    /**
     * 서로 다른 TripDayPlace 간에 이미지를 이동하는 메서드
     *
     * @param imageReq 이미지 이동에 필요한 정보를 담은 요청 DTO
     */
    public void moveImageBetweenDayPlaces(TripPlaceImageDto.ImageCrossDayMoveReq imageReq) {
        Image image = getImageFromTripPlace(imageReq.fromTripDayPlaceId(), imageReq.fromPlaceId(), imageReq.imageId());

        tripDayPlaceService.deleteImage(imageReq.fromTripDayPlaceId(), imageReq.fromPlaceId(), imageReq.imageId());
        tripDayPlaceService.saveImage(imageReq.toTripDayPlaceId(), imageReq.toPlaceId(), image);
    }

    /**
     * 특정 Place에 있는 이미지를 Unmatched(기타) 영역으로 이동시키는 메서드
     *
     * @param tripDayPlaceId TripDayPlace 문서 ID
     * @param imageReq       이미지 이동에 필요한 정보를 담은 요청 DTO
     */
    public void moveImageToUnmatched(String tripDayPlaceId, TripPlaceImageDto.ImageUnmatchedMoveReq imageReq) {
        Image image = getImageFromTripPlace(tripDayPlaceId, imageReq.placeId(), imageReq.imageId());

        tripDayPlaceService.deleteImage(tripDayPlaceId, imageReq.placeId(), imageReq.imageId());
        tripDayPlaceService.saveImageToUnmatched(tripDayPlaceId, image);
    }

    /**
     * Unmatched(기타) 영역의 이미지를 특정 Place로 이동시키는 메서드
     *
     * @param tripDayPlaceId TripDayPlace 문서 ID
     * @param imageReq       이미지 이동에 필요한 정보를 담은 요청 DTO
     */
    public void moveImageFromUnmatchedToPlace(String tripDayPlaceId, TripPlaceImageDto.ImageUnmatchedMoveReq imageReq) {
        TripDayPlace tripDayPlace = getTripDayPlaceById(tripDayPlaceId);

        Image image = tripDayPlace.getUnmatchedImages().stream()
                .filter(i -> i.getId().equals(imageReq.imageId()))
                .findFirst().orElseThrow(() -> new CustomException(ImageErrorType.NOT_FOUND));

        tripDayPlaceService.deleteImageFromUnMatched(tripDayPlaceId, imageReq.imageId());
        tripDayPlaceService.saveImage(tripDayPlaceId, imageReq.placeId(), image);
    }

    /**
     * TripDayPlace 문서 내 특정 Place의 이미지 목록에서 주어진 이미지 ID에 해당하는 이미지를 조회 메서드
     *
     * @param tripDayPlaceId TripDayPlace 문서 ID
     * @param placeId        이미지가 속한 Place ID
     * @param imageId        조회할 이미지 ID
     * @return 조회된 이미지
     */
    private Image getImageFromTripPlace(String tripDayPlaceId, String placeId, String imageId) {
        TripDayPlace tripDayPlace = getTripDayPlaceById(tripDayPlaceId);

        Place place = tripDayPlace.getPlaces().stream()
                .filter(p -> p.getId().equals(placeId))
                .findFirst().orElseThrow(() -> new CustomException(TripErrorType.PLACE_NOT_FOUND));

        return place.getImages().stream()
                .filter(i -> i.getId().equals(imageId))
                .findFirst().orElseThrow(() -> new CustomException(ImageErrorType.NOT_FOUND));
    }

    /**
     * TripDayPlace 문서를 ID로 조회하는 메서드
     *
     * @param tripDayPlaceId TripDayPlace 문서 ID
     * @return TripDayPlace 객체
     */
    private TripDayPlace getTripDayPlaceById(String tripDayPlaceId) {
        return tripDayPlaceService.readById(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(TripErrorType.DAY_PLACE_NOT_FOUND));
    }
}
