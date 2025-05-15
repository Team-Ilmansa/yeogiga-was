package kr.co.yeogiga.presentation.route.controller;

import kr.co.yeogiga.application.route.dto.RouteReq;
import kr.co.yeogiga.application.route.service.TripLeaderCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.route.api.RouteApi;
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
public class RouteController implements RouteApi {
    private final TripLeaderCommandService tripLeaderCommandService;

    @Override
    @PostMapping("/{tripId}/days/{day}/routes")
    public ResponseEntity<?> storeLeaderRoute(
            @PathVariable Long tripId,
            @PathVariable int day,
            @RequestBody RouteReq.Request routeReq
    ) {
        tripLeaderCommandService.storeLeaderRouteInRedis(tripId, day, routeReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }
}
