package kr.co.yeogiga.presentation.tripplace.controller;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.application.tripplace.service.TripPlaceEditingService;
import kr.co.yeogiga.application.tripplace.service.TripPlaceSortService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.tripplace.api.TripPlaceEditingApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceEditingController implements TripPlaceEditingApi {
    private final TripPlaceEditingService tripPlaceEditingService;
    private final TripPlaceSortService tripPlaceSortService;

    @Override
    @PostMapping("/{tripId}/days/{day}/places")
    public ResponseEntity<?> assignPlaceToDay(@PathVariable Long tripId,
                                              @PathVariable int day,
                                              @RequestBody TripPlaceReq.Request place) {

        tripPlaceEditingService.assignPlaceToDay(tripId, day, place);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @Override
    @GetMapping("/{tripId}/days/{day}/places")
    public ResponseEntity<?> getAssignedPlaces(@PathVariable Long tripId, @PathVariable int day) {
        return ResponseEntity.ok(
                SuccessResponse.from(tripPlaceEditingService.getAssignedPlaces(tripId, day))
        );
    }

    @Override
    @PutMapping("/{tripId}/days/{day}/places")
    public ResponseEntity<?> reorderPlaces(@PathVariable Long tripId,
                                          @PathVariable int day,
                                          @RequestBody TripPlaceReq.ReorderRequest request) {

        tripPlaceEditingService.reorderPlaces(tripId, day, request);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PutMapping("/{tripId}/days/{day}/places/sort")
    public ResponseEntity<?> sortDayTripPlaces(@PathVariable Long tripId,
                                               @PathVariable int day) {
        tripPlaceSortService.sortDayTripPlaces(tripId, day);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}/days/{day}/places/{placeId}")
    public ResponseEntity<?> deleteAssignedPlace(@PathVariable Long tripId,
                                                 @PathVariable int day,
                                                 @PathVariable String placeId) {

        tripPlaceEditingService.deleteAssignedPlace(tripId, day, placeId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
