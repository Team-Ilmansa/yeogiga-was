package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageAssignmentServiceLegacy;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.exception.ImageErrorType;
import kr.co.yeogiga.domain.tripplace.service.TempPlaceImagesService;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempImageAssignProcessorLegacy {
    private final TempPlaceImagesService tempPlaceImagesService;
    private final TripDayPlaceService tripDayPlaceService;
    private final TripPlaceImageAssignmentServiceLegacy tripPlaceImageAssignmentServiceLegacy;

    /**
     * 임시 저장소로부터 이미지를 불러와 TripDayPlace에 할당하는 메서드
     * - 사용이 끝난 임시 저장소 데이터를 삭제
     *
     * @param tripDayPlaceId 할당 대상이 되는 TripDayPlace의 ID
     */
    public void assignFromTempStorage(String tripDayPlaceId) {
        TempPlaceImages tempPlaceImages = tempPlaceImagesService.readByTripDayPlaceId(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(ImageErrorType.TEMP_IMAGE_NOT_FOUND));

        TripDayPlace tripDayPlace = tripDayPlaceService.readById(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_PLACE_NOT_FOUND));

        tripPlaceImageAssignmentServiceLegacy.assignImageToTripDayPlace(tripDayPlace, tempPlaceImages.getImages());

        tempPlaceImagesService.deleteById(tempPlaceImages.getId());
    }
}
