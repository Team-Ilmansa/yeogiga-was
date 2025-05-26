package kr.co.yeogiga.infrastructure.redis.constant;

public final class TripMemberTokenConstant {

    private TripMemberTokenConstant() { }

    private static final String TRIP_MEMBER_TOKEN_KEY_PREFIX = "trip:token:%d";
    public static final String TRIP_MEMBER_TOKEN_KEY_PATTERN = "trip:token:*";

    public static String tripTokenKey(Long tripId) {
        return String.format(TRIP_MEMBER_TOKEN_KEY_PREFIX, tripId);
    }
}
