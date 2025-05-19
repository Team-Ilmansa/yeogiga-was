package kr.co.yeogiga.application.trip.dto;

import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import lombok.Builder;
import java.time.LocalDateTime;

public class TripRes {

    @Builder
    public record TripSummary(
            Long tripId,
            String title,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            TravelStatus status
    ) {
        public static TripSummary from(Trip trip) {
            return TripSummary.builder()
                    .tripId(trip.getId())
                    .title(trip.getTitle())
                    .startedAt(trip.getStartedAt())
                    .endedAt(trip.getEndedAt())
                    .status(trip.getTravelStatus())
                    .build();
        }
    }
}
