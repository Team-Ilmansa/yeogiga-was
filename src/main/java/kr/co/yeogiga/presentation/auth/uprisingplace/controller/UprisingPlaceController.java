package kr.co.yeogiga.presentation.auth.uprisingplace.controller;

import kr.co.yeogiga.application.uprisingplace.service.UprisingPlaceQueryService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/uprising-places")
@RequiredArgsConstructor
public class UprisingPlaceController {
    private final UprisingPlaceQueryService uprisingPlaceQueryService;
    
    @GetMapping
    public ResponseEntity<?> getAllUprisingPlaces() {
        return ResponseEntity.ok(SuccessResponse.from(uprisingPlaceQueryService.getAllUprisingPlaces()));
    }
}
