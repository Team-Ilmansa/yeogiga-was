package kr.co.yeogiga.application.trip.dto;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class TripRes {

    @Builder
    public record TripMainInfo(
            Long tripId,
            String title,
            LocalDateTime staredAt,
            TravelStatus travelStatus,
            int day,
            List<TripPlaceRes.PlaceSummary> places
    ) {
        public static TripMainInfo from(Trip trip, int day, List<TripPlaceRes.PlaceSummary> places) {
            return TripMainInfo.builder()
                    .tripId(trip.getId())
                    .title(trip.getTitle())
                    .staredAt(trip.getStartedAt())
                    .day(day)
                    .travelStatus(trip.getTravelStatus())
                    .places(places)
                    .build();
        }
    }

}