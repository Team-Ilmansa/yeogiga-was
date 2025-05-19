package kr.co.yeogiga.presentation.tripplace.controller;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.application.tripplace.dto.VisitedMarkReq;
import kr.co.yeogiga.application.tripplace.service.TripPlaceCommandService;
import kr.co.yeogiga.application.tripplace.service.TripPlaceQueryService;
import kr.co.yeogiga.application.tripplace.service.TripPlaceSavingService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.tripplace.api.TripPlaceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripPlaceController implements TripPlaceApi {
    private final TripPlaceSavingService tripPlaceSavingService;
    private final TripPlaceCommandService tripPlaceCommandService;
    private final TripPlaceQueryService tripPlaceQueryService;

    @Override
    @PostMapping("/{tripId}/complete")
    public ResponseEntity<?> completeTrip(@PathVariable Long tripId,
                                          @RequestBody TripPlaceReq.CompleteRequest request) {

        tripPlaceSavingService.completeTrip(tripId, request.lastDay());
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @Override
    @PostMapping("/{tripId}/day-place/{tripDayPlaceId}/places")
    public ResponseEntity<?> addNewPlace(@PathVariable Long tripId,
                                         @PathVariable String tripDayPlaceId,
                                         @RequestBody TripPlaceReq.InsertRequest insertRequest) {

        tripPlaceCommandService.addNewPlace(tripDayPlaceId, insertRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @Override
    @GetMapping("/{tripId}/day-place/places")
    public ResponseEntity<?> getTripDayPlacesInfo(@PathVariable Long tripId) {
        return ResponseEntity.ok(
                SuccessResponse.from(tripPlaceQueryService.getTripDayPlacesInfo(tripId))
        );
    }

    @Override
    @GetMapping("/{tripId}/day-place/{tripDayPlaceId}/places")
    public ResponseEntity<?> getPlaceDetailsInfo(@PathVariable Long tripId,
                                                 @PathVariable String tripDayPlaceId) {

        return ResponseEntity.ok(
                SuccessResponse.from(tripPlaceQueryService.getPlaceDetailsInfo(tripDayPlaceId))
        );
    }

    @Override
    @GetMapping("/{tripId}/day-place")
    public ResponseEntity<?> getTripDaySummaries(@PathVariable Long tripId) {
        return ResponseEntity.ok(
                SuccessResponse.from(tripPlaceQueryService.getTripDaySummaries(tripId))
        );
    }

    @Override
    @PutMapping("/{tripId}/day-place/{tripDayPlaceId}/places/order")
    public ResponseEntity<?> reorderPlaces(@PathVariable Long tripId,
                                           @PathVariable String tripDayPlaceId,
                                           @RequestBody TripPlaceReq.ReorderRequest reorderRequest) {
        tripPlaceCommandService.reorderPlaces(tripDayPlaceId, reorderRequest);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @PatchMapping("/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}/mark")
    public ResponseEntity<?> markPlaceAsVisited(@PathVariable Long tripId,
                                                @PathVariable String tripDayPlaceId,
                                                @PathVariable String placeId,
                                                @RequestBody VisitedMarkReq visitedMarkReq) {
        tripPlaceCommandService.markPlaceAsVisited(tripDayPlaceId, placeId, visitedMarkReq.isVisited());
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping("/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable Long tripId,
                                         @PathVariable String tripDayPlaceId,
                                         @PathVariable String placeId) {

        tripPlaceCommandService.deletePlace(tripDayPlaceId, placeId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
