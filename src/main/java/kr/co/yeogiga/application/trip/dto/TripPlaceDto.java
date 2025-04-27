package kr.co.yeogiga.application.trip.dto;

import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;

import java.util.UUID;

public class TripPlaceDto {

    public record Request(
            String name,
            double latitude,
            double longitude,
            String placeType
    ) {
        public StoredFormat toStoredFormat() {
            return new StoredFormat(
                    UUID.randomUUID().toString(),
                    name,
                    latitude,
                    longitude,
                    PlaceCategory.fromLabel(placeType)
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
}
