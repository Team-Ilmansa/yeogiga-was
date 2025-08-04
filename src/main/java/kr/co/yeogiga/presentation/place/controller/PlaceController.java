package kr.co.yeogiga.presentation.place.controller;

import kr.co.yeogiga.application.place.application.PlaceSearchService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places")
public class PlaceController {
    private final PlaceSearchService placeSearchService;
    
    @GetMapping("/search")
    public ResponseEntity<?> searchPlace(@RequestParam(name = "place") String place) {
        return ResponseEntity
                .ok(SuccessResponse.from(placeSearchService.searchPlace(place)));
    }
}
