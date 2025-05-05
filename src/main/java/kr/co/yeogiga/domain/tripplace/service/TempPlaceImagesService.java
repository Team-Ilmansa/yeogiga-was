package kr.co.yeogiga.domain.tripplace.service;

import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.repository.TempPlaceImagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempPlaceImagesService {
    private final TempPlaceImagesRepository tempPlaceImagesRepository;

    public void saveImage(String placeId, Image image) {
        tempPlaceImagesRepository.saveImage(placeId, image);
    }
}
