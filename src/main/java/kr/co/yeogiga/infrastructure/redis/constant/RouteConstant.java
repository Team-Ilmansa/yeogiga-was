package kr.co.yeogiga.infrastructure.redis.constant;

public final class RouteConstant {

    private RouteConstant() { }

    private static final String TRIP_ROUTE_KEY_PREFIX = "trip:route:%d:%d";
    public static final String TRIP_ROUTE_KEY_PATTERN = "trip:route:*:*";

    public static String TripRouteKey(Long tripId, int day) {
        return String.format(TRIP_ROUTE_KEY_PREFIX, tripId, day);
    }
}
