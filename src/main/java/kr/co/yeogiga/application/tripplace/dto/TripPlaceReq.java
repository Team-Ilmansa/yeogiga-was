package kr.co.yeogiga.application.tripplace.dto;

import kr.co.yeogiga.domain.trip.entity.Place;
import kr.co.yeogiga.domain.trip.entity.TripDay;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TripPlaceReq {

    @Builder
    public record Request(
            String name,
            double latitude,
            double longitude,
            PlaceCategory placeType
    ) {
        public Place toEntity(TripDay tripDay, int sortOrder) {
            return Place.builder()
                    .name(name)
                    .latitude(latitude)
                    .longitude(longitude)
                    .sortOrder(sortOrder)
                    .placeType(placeType)
                    .tripDay(tripDay)
                    .build();
        }

        public TripPlaceReq.StoredFormat toStoredFormat() {
            return new TripPlaceReq.StoredFormat(
                    UUID.randomUUID().toString(),
                    name,
                    latitude,
                    longitude,
                    placeType
            );
        }
    }

    public record StoredFormat(
            String id,
            String name,
            double latitude,
            double longitude,
            PlaceCategory placeCategory
    ) { }

    public record CompleteRequest(
            int lastDay
    ) { }

    public record ReorderRequest(
            List<String> orderedPlaceIds
    ) { }
}
