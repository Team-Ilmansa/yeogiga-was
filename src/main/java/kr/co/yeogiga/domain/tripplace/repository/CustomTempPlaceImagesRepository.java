package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.Image;

public interface CustomTempPlaceImagesRepository {
    void saveImage(String placeId, Image image);
}
