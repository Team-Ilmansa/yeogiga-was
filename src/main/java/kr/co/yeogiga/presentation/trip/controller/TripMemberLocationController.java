package kr.co.yeogiga.presentation.trip.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.trip.dto.TripMemberLocationDto;
import kr.co.yeogiga.application.trip.service.TripMemberLocationCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripMemberLocationController {
    private final TripMemberLocationCommandService tripMemberLocationCommandService;

    @PostMapping("/{tripId}/members/location")
    public ResponseEntity<?> saveLocation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody TripMemberLocationDto.Request request
    ) {
        tripMemberLocationCommandService.saveLocation(tripId, userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }
}
