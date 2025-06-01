package kr.co.yeogiga.application.fcm.constant;

public final class FcmConstant {

    private FcmConstant() { }

    private static final String FCM_TITLE = "%s 여행이 시작되었습니다!";
    public static final String FCM_BODY = "앱을 열어 여행 확인해보세요.";

    private static final String PIN_FCM_TITLE = "%s - 집결지 요청";

    public static String formatTitle(String title) {
        return String.format(FCM_TITLE, title);
    }

    public static String formatPinTitle(String title) {
        return String.format(PIN_FCM_TITLE, title);
    }
}
