package kr.co.yeogiga.domain.trip.type;

import java.time.LocalDateTime;

public enum TravelStatus {
    SETTING,
    PLANNED,
    IN_PROGRESS,
    COMPLETED;

    public static TravelStatus resolveStatus(LocalDateTime start, LocalDateTime end) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(start)) {
            return PLANNED;
        }

        if (start.isBefore(now) && end.isAfter(now)) {
            return IN_PROGRESS;
        }

        if (now.isAfter(end)) {
            return COMPLETED;
        }

        return PLANNED;
    }
}
