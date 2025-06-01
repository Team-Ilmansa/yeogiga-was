package kr.co.yeogiga.domain.trip.dto;

public record TripFcmTokenInfoDto (
        Long tripId,
        String title,
        String fcmToken
) {
}
