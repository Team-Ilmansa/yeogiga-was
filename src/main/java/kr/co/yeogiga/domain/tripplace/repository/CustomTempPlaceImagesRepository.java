package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.placeimage.entity.Image;

import java.util.List;

public interface CustomTempPlaceImagesRepository {
    void saveImage(String tripDayPlaceId, Image image);
    void deleteImages(String id, List<String> imageIds);
}
