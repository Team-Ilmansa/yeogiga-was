package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.tripplace.service.TripPlaceImageService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import kr.co.yeogiga.domain.tripplace.exception.ImageErrorType;
import kr.co.yeogiga.domain.tripplace.service.TempPlaceImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempImageAssignProcessor {
    private final TempPlaceImagesService tempPlaceImagesService;
    private final TripPlaceImageService tripPlaceImageService;

    /**
     * 임시 저장소로부터 이미지를 불러와 TripDayPlace에 할당하는 메서드
     * - 사용이 끝난 임시 저장소 데이터를 삭제
     *
     * @param tripDayPlaceId 할당 대상이 되는 TripDayPlace의 ID
     */
    public void assignFromTempStorage(String tripDayPlaceId) {
        TempPlaceImages tempPlaceImages = tempPlaceImagesService.readByTripDayPlaceId(tripDayPlaceId)
                .orElseThrow(() -> new CustomException(ImageErrorType.NOT_FOUND_TEMP_IMAGE_STORE));

        tripPlaceImageService.assignImageToTripDayPlace(tripDayPlaceId, tempPlaceImages.getImages());

        tempPlaceImagesService.deleteById(tempPlaceImages.getId());
    }
}
