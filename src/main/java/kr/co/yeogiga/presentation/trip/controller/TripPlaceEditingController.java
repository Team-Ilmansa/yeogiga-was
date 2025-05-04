package kr.co.yeogiga.presentation.trip.controller;

import kr.co.yeogiga.application.trip.dto.TripPlaceReq;
import kr.co.yeogiga.application.trip.service.TripPlaceEditingService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.trip.api.TripPlaceEditingApi;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceEditingController implements TripPlaceEditingApi {
    private final TripPlaceEditingService tripPlaceEditingService;

    @PostMapping("/{tripId}/temp-places")
    public ResponseEntity<?> addTempPlace(@PathVariable Long tripId,
                                          @RequestBody TripPlaceReq.Request request) {
        tripPlaceEditingService.addTempPlace(tripId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @GetMapping("/{tripId}/temp-places")
    public ResponseEntity<?> getTempPlaces(@PathVariable Long tripId) {
        return ResponseEntity.ok(
                SuccessResponse.from(tripPlaceEditingService.getTempPlaces(tripId))
        );
    }

    @DeleteMapping("/{tripId}/temp-places/{placeId}")
    public ResponseEntity<?> deleteTempPlace(@PathVariable Long tripId,
                                             @PathVariable String placeId) {
        tripPlaceEditingService.deleteTempPlace(tripId, placeId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PostMapping("/{tripId}/days/{day}/places/{placeId}")
    public ResponseEntity<?> assignPlaceToDay(@PathVariable Long tripId,
                                              @PathVariable int day,
                                              @PathVariable String placeId) {

        tripPlaceEditingService.assignPlaceToDay(tripId, day, placeId);
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
    public ResponseEntity<?> updatePlaces(@PathVariable Long tripId,
                                          @PathVariable int day,
                                          @RequestBody List<TripPlaceReq.Request> requests) {

        tripPlaceEditingService.updatePlaces(tripId, day, requests);
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
