package kr.co.yeogiga.infrastructure.redis.constant;

public final class PlaceConstant {

    private PlaceConstant() { }

    private static final String LIST_KEY_FORMAT = "trip:%d:day:%d:places";
    private static final String SET_KEY_FORMAT = "trip:%d:day:%d:places:set";

    public static String listKey(Long tripId, int day) {
        return String.format(LIST_KEY_FORMAT, tripId, day);
    }

    public static String setKey(Long tripId, int day) {
        return String.format(SET_KEY_FORMAT, tripId, day);
    }
}
