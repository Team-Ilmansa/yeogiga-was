package kr.co.yeogiga.infrastructure.redis.constant;

public final class PinConstant {

    private PinConstant() { }

    private static final String PIN_KEY_FORMAT = "pin:trip:%d";

    public static String pinKey(Long tripId) {
        return String.format(PIN_KEY_FORMAT, tripId);
    }
}
