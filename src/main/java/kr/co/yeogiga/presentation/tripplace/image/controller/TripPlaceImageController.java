package kr.co.yeogiga.presentation.tripplace.image.controller;

import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDto;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageDeleteService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageMovementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.tripplace.image.api.TripPlaceImageApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceImageController implements TripPlaceImageApi {
    private final TripPlaceImageMovementService tripPlaceImageMovementService;
    private final TripPlaceImageDeleteService tripPlaceImageDeleteService;

    @Override
    @PatchMapping("/{tripId}/images/{tripDayPlaceId}/move")
    public ResponseEntity<?> moveImageToAnotherPlace(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageDto.ImageMoveReq imageReq
    ) {
        tripPlaceImageMovementService.moveImageToAnotherPlace(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/images/move-between-days")
    public ResponseEntity<?> moveImageBetweenDayPlaces(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageDto.ImageCrossDayMoveReq imageReq
    ) {
        tripPlaceImageMovementService.moveImageBetweenDayPlaces(imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/images/{tripDayPlaceId}/move-to-unmatched")
    public ResponseEntity<?> moveImageToUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageDto.ImageUnmatchedMoveReq imageReq
    ) {
        tripPlaceImageMovementService.moveImageToUnmatched(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/images/{tripDayPlaceId}/move-from-unmatched")
    public ResponseEntity<?> moveImageFromUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageDto.ImageUnmatchedMoveReq imageReq
    ) {
        tripPlaceImageMovementService.moveImageFromUnmatchedToPlace(tripDayPlaceId, imageReq);
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
        tripPlaceImageDeleteService.deleteSingleImage(tripDayPlaceId, imageId, deleteReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}/images")
    public ResponseEntity<?> deleteMultipleImages(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageDeleteDto.MultiDeleteReq deleteReq
    ) {
        tripPlaceImageDeleteService.deleteMultipleImages(tripId, deleteReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
