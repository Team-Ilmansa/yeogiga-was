package kr.co.yeogiga.presentation.trip.controller;

import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
import kr.co.yeogiga.application.trip.service.TripPlaceSavingService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.trip.api.TripPlaceSavingApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceSavingController implements TripPlaceSavingApi {
    private final TripPlaceSavingService tripPlaceSavingService;

    @Override
    @PostMapping("/{tripId}/complete")
    public ResponseEntity<?> completeTrip(@PathVariable Long tripId,
                                          @RequestBody TripPlaceDto.CompleteRequest request) {

        tripPlaceSavingService.completeTrip(tripId, request.lastDay());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.ok());
    }
}
