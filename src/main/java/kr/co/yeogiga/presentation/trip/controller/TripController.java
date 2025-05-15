package kr.co.yeogiga.presentation.trip.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.application.trip.service.TripCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.presentation.trip.api.TripApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripController implements TripApi {
    private final TripCommandService tripCommandService;

    @Override
    @PostMapping
    public ResponseEntity<?> createTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TripReq.Creation request
    ) {
        tripCommandService.create(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @PutMapping("/{tripId}/time")
    public ResponseEntity<?> updateTripTime(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId,
            @RequestBody TripReq.Time request
    ) {
        tripCommandService.updateTime(tripId, userDetails.getUserId(), request);
        return ResponseEntity.ok(SuccessResponse.ok());

    }
}
