package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripCommandService {
    private final TripService tripService;

    public void create(Long leaderId, TripReq.Creation creationRequest) {
        Trip trip = Trip.builder()
                .title(creationRequest.title())
                .city(creationRequest.city())
                .leaderId(leaderId)
                .travelStatus(TravelStatus.PLANNED)
                .build();

        tripService.save(trip);
    }
}
