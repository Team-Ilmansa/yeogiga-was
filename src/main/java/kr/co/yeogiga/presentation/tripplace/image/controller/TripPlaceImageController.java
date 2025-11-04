package kr.co.yeogiga.presentation.tripplace.image.controller;

import kr.co.yeogiga.application.tripplace.image.dto.FavoriteImageReq;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageReq;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageCommandServiceLegacy;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageMovementServiceLegacy;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageQueryServiceLegacy;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageReassignmentServiceLegacy;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.tripplace.image.api.TripPlaceImageApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceImageController implements TripPlaceImageApi {
    private final TripPlaceImageMovementServiceLegacy tripPlaceImageMovementServiceLegacy;
    private final TripPlaceImageCommandServiceLegacy tripPlaceImageCommandServiceLegacy;
    private final TripPlaceImageQueryServiceLegacy tripPlaceImageQueryServiceLegacy;
    private final TripPlaceImageReassignmentServiceLegacy tripPlaceImageReassignmentServiceLegacy;

    @Override
    @GetMapping("/{tripId}/day-place/images/day/{day}")
    public ResponseEntity<?> getTripDayImageInfo(
            @PathVariable Long tripId,
            @PathVariable int day
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(
                        tripPlaceImageQueryServiceLegacy.getTripDayImageInfo(tripId, day)
                )
        );
    }

    @GetMapping("/{tripId}/day-place/images/day/{day}/with-place")
    public ResponseEntity<?> getTripDayImageInfoWithPlaceId(
            @PathVariable Long tripId,
            @PathVariable int day
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(
                        tripPlaceImageQueryServiceLegacy.getTripDayImageInfoWithPlaceId(tripId, day)
                )
        );
    }

    @Override
    @GetMapping("/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}/images")
    public ResponseEntity<?> getPlaceInfo(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @PathVariable String placeId
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(
                        tripPlaceImageQueryServiceLegacy.getPlaceImageInfo(tripDayPlaceId, placeId)
                )
        );
    }

    @Override
    @GetMapping("/{tripId}/day-place/{tripDayPlaceId}/unmatched-images")
    public ResponseEntity<?> getUnmatchedImageInfo(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(
                        tripPlaceImageQueryServiceLegacy.getUnmatchedImageInfo(tripDayPlaceId)
                )
        );
    }

    @Override
    @GetMapping("/{tripId}/day-place/{tripDayPlaceId}/images/favorite")
    public ResponseEntity<?> getFavoriteImages(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(
                        tripPlaceImageQueryServiceLegacy.getFavoriteImages(tripDayPlaceId)
                )
        );
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/move")
    public ResponseEntity<?> moveImageToAnotherPlace(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageReq.ImageMove imageReq
    ) {
        tripPlaceImageMovementServiceLegacy.moveImageToAnotherPlace(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/images/move")
    public ResponseEntity<?> moveImageBetweenDayPlaces(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageReq.ImageCrossDayMove imageReq
    ) {
        tripPlaceImageMovementServiceLegacy.moveImageBetweenDayPlaces(imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/unmatch")
    public ResponseEntity<?> moveImageToUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageReq.ImageUnmatchedMove imageReq
    ) {
        tripPlaceImageMovementServiceLegacy.moveImageToUnmatched(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/rematch")
    public ResponseEntity<?> moveImageFromUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageReq.ImageUnmatchedMove imageReq
    ) {
        tripPlaceImageMovementServiceLegacy.moveImageFromUnmatchedToPlace(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/re-assign")
    public ResponseEntity<?> reassignImages(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId
    ) {
        tripPlaceImageReassignmentServiceLegacy.reassignImagesToTripDayPlace(tripDayPlaceId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/{imageId}/favorite")
    public ResponseEntity<?> updateImageFavoriteStatus(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @PathVariable String imageId,
            @RequestBody FavoriteImageReq favoriteImageReq
    ) {
        tripPlaceImageCommandServiceLegacy.updateImageFavoriteStatus(tripDayPlaceId, imageId, favoriteImageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}/day-place/{tripDayPlaceId}/images/{imageId}")
    public ResponseEntity<?> deleteSingleImage(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @PathVariable String imageId,
            @RequestBody TripPlaceImageDeleteDto.SingleDeleteReq deleteReq
    ) {
        tripPlaceImageCommandServiceLegacy.deleteSingleImage(tripDayPlaceId, imageId, deleteReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}/images")
    public ResponseEntity<?> deleteMultipleImages(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageDeleteDto.MultiDeleteReq deleteReq
    ) {
        tripPlaceImageCommandServiceLegacy.deleteMultipleImages(tripId, deleteReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
