package kr.co.yeogiga.presentation.tripplace.image.controller;

import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageReq;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageCommandService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageMovementService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageQueryService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageReassignmentService;
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
    private final TripPlaceImageMovementService tripPlaceImageMovementService;
    private final TripPlaceImageCommandService tripPlaceImageCommandService;
    private final TripPlaceImageQueryService tripPlaceImageQueryService;
    private final TripPlaceImageReassignmentService tripPlaceImageReassignmentService;

    @Override
    @GetMapping("/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}/images")
    public ResponseEntity<?> getPlaceInfo(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @PathVariable String placeId
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(
                        tripPlaceImageQueryService.getPlaceImageInfo(tripDayPlaceId, placeId)
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
                        tripPlaceImageQueryService.getUnmatchedImageInfo(tripDayPlaceId)
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
        tripPlaceImageMovementService.moveImageToAnotherPlace(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/images/move")
    public ResponseEntity<?> moveImageBetweenDayPlaces(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageReq.ImageCrossDayMove imageReq
    ) {
        tripPlaceImageMovementService.moveImageBetweenDayPlaces(imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/unmatch")
    public ResponseEntity<?> moveImageToUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageReq.ImageUnmatchedMove imageReq
    ) {
        tripPlaceImageMovementService.moveImageToUnmatched(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/rematch")
    public ResponseEntity<?> moveImageFromUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageReq.ImageUnmatchedMove imageReq
    ) {
        tripPlaceImageMovementService.moveImageFromUnmatchedToPlace(tripDayPlaceId, imageReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/images/re-assign")
    public ResponseEntity<?> reassignImages(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId
    ) {
        tripPlaceImageReassignmentService.reassignImagesToTripDayPlace(tripDayPlaceId);
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
        tripPlaceImageCommandService.deleteSingleImage(tripDayPlaceId, imageId, deleteReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}/images")
    public ResponseEntity<?> deleteMultipleImages(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageDeleteDto.MultiDeleteReq deleteReq
    ) {
        tripPlaceImageCommandService.deleteMultipleImages(tripId, deleteReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
