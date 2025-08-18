package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.image.dto.ImageDeleteDto;
import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.tripplace.service.TempPlaceImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempPlaceImagesCommandService {
    private final TempPlaceImagesService tempPlaceImagesService;
    private final ImageDeleteProcessor imageDeleteProcessor;

    /**
     * 지정된 tripDayPlaceId 가진 TempPlaceImages 문서에 이미지를 추가하는 메서드
     *
     * @param tripDayPlaceId 추가할 이미지가 속할 여행일차 ID
     * @param image          MongoDB에 저장할 이미지 객체
     */
    public void addImageToTripDayPlace(String tripDayPlaceId, Image image) {
        tempPlaceImagesService.saveImage(tripDayPlaceId, image);
    }

    /**
     * TempPlaceImages(임시 저장소) 문서에서 이미지를 제거하는 메서드
     * - MongoDB 문서에서 이미지 삭제 - imageIds
     * - AWS S3에서 이미지 삭제 - urls
     *
     * @param tempPlaceImageId 임시 저장소 ID
     * @param deleteDto        이미지 삭제 정보 객체
     */
    public void removeTempImages(String tempPlaceImageId, ImageDeleteDto deleteDto) {
        tempPlaceImagesService.deleteImages(tempPlaceImageId, deleteDto.imageIds());
        imageDeleteProcessor.process(deleteDto.urls());
    }
}
