package kr.co.yeogiga.presentation.auth.uprisingplace.controller;

import kr.co.yeogiga.application.uprisingplace.service.UprisingPlaceQueryService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.auth.uprisingplace.api.UprisingPlaceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/uprising-places")
@RequiredArgsConstructor
public class UprisingPlaceController implements UprisingPlaceApi {
    private final UprisingPlaceQueryService uprisingPlaceQueryService;
    
    @Override
    @GetMapping
    public ResponseEntity<?> getAllUprisingPlaces() {
        return ResponseEntity.ok(SuccessResponse.from(uprisingPlaceQueryService.getAllUprisingPlaces()));
    }
}
