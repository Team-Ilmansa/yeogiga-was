package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripPlaceImageDeleteService {
    private final TripDayPlaceService tripDayPlaceService;

    public void deleteSingleImage(String tripDayPlaceId, String imageId, TripPlaceImageDeleteDto.SingleDeleteReq deleteReq) {
        switch (deleteReq.deleteType()) {
            case PLACE -> tripDayPlaceService.deleteImage(
                    tripDayPlaceId, deleteReq.placeId(), imageId
            );

            case UNMATCHED -> tripDayPlaceService.deleteImageFromUnMatched(
                    tripDayPlaceId, imageId
            );
        }
    }

    public void deleteMultipleImages(Long tripId, TripPlaceImageDeleteDto.MultiDeleteReq deleteReq) {
        tripDayPlaceService.deleteImagesByTripId(tripId, deleteReq.imageIds());
    }
}
