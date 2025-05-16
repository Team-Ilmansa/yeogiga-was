package kr.co.yeogiga.presentation.trip.controller;

import kr.co.yeogiga.application.trip.service.TripMemberCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.presentation.trip.api.TripMemberApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripMemberController implements TripMemberApi {
    private final TripMemberCommandService tripMemberCommandService;

    @Override
    @PostMapping("/{tripId}/members")
    public ResponseEntity<?> joinTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId
    ) {
        tripMemberCommandService.joinTrip(tripId, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }
}
