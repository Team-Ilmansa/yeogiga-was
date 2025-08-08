package kr.co.yeogiga.presentation.trip.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.application.trip.dto.TripRes;
import kr.co.yeogiga.application.trip.service.TripCommandService;
import kr.co.yeogiga.application.trip.service.TripQueryService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.domain.trip.dto.TripDto;
import kr.co.yeogiga.presentation.trip.api.TripApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripController implements TripApi {
    private final TripQueryService tripQueryService;
    private final TripCommandService tripCommandService;

    @Override
    @GetMapping("/main")
    public ResponseEntity<?> getMainTrip(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails
    ) {
        TripRes.TripMainInfo tripMainInfo = tripQueryService.getTripMainInfo(userDetails.getUserId());
        return ResponseEntity.ok().body(SuccessResponse.from(tripMainInfo));
    }

    @Override
    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok()
                .body(SuccessResponse.from(tripQueryService.getTrip(tripId)));
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getAllTrip(
            @RequestParam(name = "status", required = false) String status,
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails
    ) {
        List<TripDto.Summary> result = tripQueryService.getAllTrip(userDetails.getUserId(), status);
        return ResponseEntity.ok().body(SuccessResponse.from(result));
    }

    @Override
    @GetMapping("/setting")
    public ResponseEntity<?> getSettingTrip(@AuthenticationPrincipal CustomUserDetailsImpl userDetails) {
        return ResponseEntity.ok()
                .body(SuccessResponse.from(tripQueryService.getSettingTrip(userDetails.getUserId())));
    }

    @Override
    @PostMapping
    public ResponseEntity<?> createTrip(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody TripReq.Creation request
    ) {
        Long tripId = tripCommandService.create(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created(Map.of("tripId", tripId)));
    }

    @Override
    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTripInfo(
        @PathVariable Long tripId,
        @Valid @RequestBody TripReq.Update updateRequest
    ) {
        tripCommandService.updateTripInfo(tripId, updateRequest);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @PutMapping("/{tripId}/time")
    public ResponseEntity<?> updateTripTime(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody TripReq.Time request
    ) {
        tripCommandService.updateTime(tripId, userDetails.getUserId(), request);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> removeTrip(@PathVariable Long tripId) {
        tripCommandService.removeTrip(tripId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
