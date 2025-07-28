package kr.co.yeogiga.domain.trip.dto;

import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class TripDto {
    
    @Builder
    public record Summary(
            Long tripId,
            String title,
            String city,
            Long leaderId,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            TravelStatus status,
            List<MemberInfo> members
    ) {
        public static Summary from(Trip trip, List<MemberInfo> members) {
            return Summary.builder()
                    .tripId(trip.getId())
                    .title(trip.getTitle())
                    .city(trip.getCity())
                    .leaderId(trip.getLeaderId())
                    .startedAt(trip.getStartedAt())
                    .endedAt(trip.getEndedAt())
                    .status(trip.getTravelStatus())
                    .members(members)
                    .build();
        }
    }
    
    @Builder
    public record MemberInfo(
            Long userId,
            String nickname,
            String imageUrl
    ) { }
}
