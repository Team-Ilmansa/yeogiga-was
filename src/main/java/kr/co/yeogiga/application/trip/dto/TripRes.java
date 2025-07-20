package kr.co.yeogiga.application.trip.dto;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.User;
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

    @Builder
    public record TripSummary(
            Long tripId,
            String title,
            String city,
            Long leaderId,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            TravelStatus status,
            List<TripMemberRes.MemberInfo> members
    ) {
        public static TripSummary from(Trip trip, List<TripMemberRes.MemberInfo> members) {
            return TripSummary.builder()
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
    public record SettingTripInfo(
            Long tripId,
            String title,
            TravelStatus status
    ) {
        public static SettingTripInfo from(Trip trip) {
            return SettingTripInfo.builder()
                    .tripId(trip.getId())
                    .title(trip.getTitle())
                    .status(trip.getTravelStatus())
                    .build();
        }
    }
}
