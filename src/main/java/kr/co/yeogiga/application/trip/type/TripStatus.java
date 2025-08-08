package kr.co.yeogiga.application.trip.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TripStatus {
    SETTING,
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    ALL
}
