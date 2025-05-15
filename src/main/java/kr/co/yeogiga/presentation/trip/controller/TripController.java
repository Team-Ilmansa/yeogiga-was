package kr.co.yeogiga.presentation.trip.controller;

import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.application.trip.service.TripCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripController {
    private final TripCommandService tripCommandService;

    @PostMapping
    public ResponseEntity<?> createTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TripReq.Creation request
    ) {
        tripCommandService.create(userDetails.getUserId(), request);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
