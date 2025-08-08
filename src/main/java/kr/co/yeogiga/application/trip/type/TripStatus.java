package kr.co.yeogiga.application.trip.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TripStatus {
    SETTING,
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    ALL;
    
    public static TripStatus resolveStatus(String value) {
        if (value == null) {
            return ALL;
        }
        
        for (TripStatus status : TripStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        
        return null;
    }
}
