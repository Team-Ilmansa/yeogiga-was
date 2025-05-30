package kr.co.yeogiga.infrastructure.redis.constant;

public final class TripMemberLocationConstant {

    private TripMemberLocationConstant() { }

    private static final String TRIP_MEMBER_LOCATION_KEY_PREFIX = "trip:%d:location";
    private static final String TRIP_MEMBER_LOCATION_SUBKEY_PREFIX = "user:%d";

    public static String tripMemberLocationKey(Long tripId) {
        return String.format(TRIP_MEMBER_LOCATION_KEY_PREFIX, tripId);
    }

    public static String tripMemberLocationSubKey(Long userId) {
        return String.format(TRIP_MEMBER_LOCATION_SUBKEY_PREFIX, userId);
    }
}
