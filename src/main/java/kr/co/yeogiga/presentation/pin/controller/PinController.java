package kr.co.yeogiga.presentation.pin.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.pin.dto.PinReq;
import kr.co.yeogiga.application.pin.service.PinCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
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
public class PinController {
    private final PinCommandService pinCommandService;

    @PostMapping("/{tripId}/pin")
    public ResponseEntity<?> createPin(
            @PathVariable Long tripId,
            @Valid @RequestBody PinReq.Creation request
    ) {
        pinCommandService.createPin(tripId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.created());
    }
}
