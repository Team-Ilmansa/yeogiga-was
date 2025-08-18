package kr.co.yeogiga.domain.tripplace.service;

import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import kr.co.yeogiga.domain.tripplace.repository.TempPlaceImagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TempPlaceImagesService {
    private final TempPlaceImagesRepository tempPlaceImagesRepository;

    public void saveImage(String tripDayPlaceId, Image image) {
        tempPlaceImagesRepository.saveImage(tripDayPlaceId, image);
    }

    public Optional<TempPlaceImages> readByTripDayPlaceId(String tripDayPlaceId) {
        return tempPlaceImagesRepository.findByTripDayPlaceId(tripDayPlaceId);
    }

    public void deleteById(String id) {
        tempPlaceImagesRepository.deleteById(id);
    }

    public void deleteImages(String id, List<String> imageIds) {
        tempPlaceImagesRepository.deleteImages(id, imageIds);
    }
}
