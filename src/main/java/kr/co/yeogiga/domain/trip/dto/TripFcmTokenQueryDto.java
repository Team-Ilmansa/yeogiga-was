package kr.co.yeogiga.domain.trip.dto;

import java.time.LocalDateTime;

public record TripFcmTokenQueryDto(
        Long tripId,
        String fcmToken,
        LocalDateTime endedAt
) {
}
