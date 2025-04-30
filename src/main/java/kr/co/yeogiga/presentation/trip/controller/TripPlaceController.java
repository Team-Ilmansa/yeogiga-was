package kr.co.yeogiga.presentation.trip.controller;

import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
import kr.co.yeogiga.application.trip.service.TripPlaceCommandService;
import kr.co.yeogiga.application.trip.service.TripPlaceSavingService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.trip.api.TripPlaceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceController implements TripPlaceApi {
    private final TripPlaceSavingService tripPlaceSavingService;
    private final TripPlaceCommandService tripPlaceCommandService;

    @Override
    @PostMapping("/{tripId}/complete")
    public ResponseEntity<?> completeTrip(@PathVariable Long tripId,
                                          @RequestBody TripPlaceDto.CompleteRequest request) {

        tripPlaceSavingService.completeTrip(tripId, request.lastDay());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @PostMapping("/{tripId}/day-place/{tripDayPlaceId}/places")
    public ResponseEntity<?> addNewPlace(@PathVariable String tripId,
                                         @PathVariable String tripDayPlaceId,
                                         @RequestBody TripPlaceDto.InsertRequest insertRequest) {

        tripPlaceCommandService.addNewPlace(tripDayPlaceId, insertRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @PutMapping("/{tripId}/day-place/{tripDayPlaceId}/places/order")
    public ResponseEntity<?> reorderPlaces(@PathVariable String tripId,
                                           @PathVariable String tripDayPlaceId,
                                           @RequestBody TripPlaceDto.ReorderRequest reorderRequest) {
        tripPlaceCommandService.reorderPlaces(tripDayPlaceId, reorderRequest);
        return ResponseEntity.ok(SuccessResponse.ok());
    }


    @DeleteMapping("/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable String tripId,
                                         @PathVariable String tripDayPlaceId,
                                         @PathVariable String placeId) {

        tripPlaceCommandService.deletePlace(tripDayPlaceId, placeId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
