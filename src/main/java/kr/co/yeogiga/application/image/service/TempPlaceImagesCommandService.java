package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.service.TempPlaceImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempPlaceImagesCommandService {
    private final TempPlaceImagesService tempPlaceImagesService;

    /**
     * 지정된 placeId를 가진 TempPlaceImages 문서에 이미지를 추가하는 메서드
     *
     * @param tripDayPlaceId 추가할 이미지가 속할 여행일차 ID
     * @param image          MongoDB에 저장할 이미지 객체
     */
    public void addImageToPlace(String tripDayPlaceId, Image image) {
        tempPlaceImagesService.saveImage(tripDayPlaceId, image);
    }
}
