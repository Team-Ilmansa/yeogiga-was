package kr.co.yeogiga.application.fcm.constant;

public final class FcmConstant {

    private FcmConstant() { }

    private static final String FCM_TITLE = "%s 여행이 시작되었습니다!";
    public static final String FCM_BODY = "앱을 열어 여행 일정을 확인해보세요.";

    public static String formatTitle(String title) {
        return String.format(FCM_TITLE, title);
    }
}
