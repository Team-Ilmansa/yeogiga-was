package kr.co.yeogiga.presentation.image.controller;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceImageDto;
import kr.co.yeogiga.application.tripplace.service.TripPlaceImageMovementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.image.api.TripPlaceImageMovementApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceImageMovementController implements TripPlaceImageMovementApi {
    private final TripPlaceImageMovementService tripPlaceImageMovementService;

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
}
