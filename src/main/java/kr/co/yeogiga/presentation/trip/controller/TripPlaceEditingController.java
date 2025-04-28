package kr.co.yeogiga.presentation.trip.controller;

import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
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

    @Override
    @PostMapping("/{tripId}/days/{day}/places")
    public ResponseEntity<?> addPlace(@PathVariable Long tripId,
                                      @PathVariable int day,
                                      @RequestBody TripPlaceDto.Request request) {

        tripPlaceEditingService.addPlaceInEditing(tripId, day, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.ok());
    }

    @Override
    @GetMapping("/{tripId}/days/{day}/places")
    public ResponseEntity<?> getPlaces(@PathVariable Long tripId, @PathVariable int day) {
        return ResponseEntity.ok(
                SuccessResponse.from(tripPlaceEditingService.getPlacesInEditing(tripId, day))
        );
    }

    @Override
    @PutMapping("/{tripId}/days/{day}/places")
    public ResponseEntity<?> updatePlaces(@PathVariable Long tripId,
                                          @PathVariable int day,
                                          @RequestBody List<TripPlaceDto.Request> requests) {

        tripPlaceEditingService.updatePlacesInEditing(tripId, day, requests);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}/days/{day}/places/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable Long tripId,
                                         @PathVariable int day,
                                         @PathVariable String placeId) {

        tripPlaceEditingService.deletePlaceInEditing(tripId, day, placeId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
