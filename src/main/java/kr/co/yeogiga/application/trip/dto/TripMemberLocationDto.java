package kr.co.yeogiga.application.trip.dto;

public class TripMemberLocationDto {

    public record Request(
            Double latitude,
            Double longitude
    ) {
        public StoredFormat toStoredFormat(Long userId) {
            return new StoredFormat(latitude, longitude, userId);
        }
    }

    public record StoredFormat(
            Double latitude,
            Double longitude,
            Long userId
    ) { }
}
