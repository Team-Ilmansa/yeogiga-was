package kr.co.yeogiga.presentation.route.controller;

import kr.co.yeogiga.application.route.service.TripRouteQueryService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.route.api.RouteApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class RouteController implements RouteApi {
    private final TripRouteQueryService tripRouteQueryService;

    @Override
    @GetMapping("/{tripId}/routes")
    public ResponseEntity<?> getTripRoutes(
            @PathVariable Long tripId
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(tripRouteQueryService.getTripRoutes(tripId))
        );
    }
}
