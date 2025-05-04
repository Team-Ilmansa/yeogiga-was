package kr.co.yeogiga.infrastructure.redis.constant;

public final class PlaceConstant {

    private PlaceConstant() { }

    private static final String DAY_PLACES_KEY_FORMAT = "trip:%d:day:%d:places";
    private static final String DAY_PLACE_SET_KEY_FORMAT = "trip:%d:day:%d:places:set";
    private static final String TEMP_LIST_KEY_FORMAT = "trip:%d:temp:places";

    public static String dayPlacesKey(Long tripId, int day) {
        return String.format(DAY_PLACES_KEY_FORMAT, tripId, day);
    }

    public static String dayPlaceSetKey(Long tripId, int day) {
        return String.format(DAY_PLACE_SET_KEY_FORMAT, tripId, day);
    }

    public static String tempListKey(Long tripId) {
        return String.format(TEMP_LIST_KEY_FORMAT, tripId);
    }
}
