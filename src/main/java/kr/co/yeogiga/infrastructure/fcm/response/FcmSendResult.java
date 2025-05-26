package kr.co.yeogiga.infrastructure.fcm.response;

/**
 * FCM 푸시 알림 전송 결과를 나타내는 클래스
 *
 * @param success           전송 성공 여부
 * @param shouldRemoveToken 토큰 삭제 필요 여부 (UNREGISTERED, INVALID_ARGUMENT 등)
 * @param message           전송 결과에 대한 메시지 (성공/실패 이유 등)
 */
public record FcmSendResult(
        boolean success,
        boolean shouldRemoveToken,
        String message
) {
    public static FcmSendResult success(String message) {
        return new FcmSendResult(true, false, message);
    }

    public static FcmSendResult invalidToken(String message) {
        return new FcmSendResult(false, true, message);
    }

    public static FcmSendResult failure(String message) {
        return new FcmSendResult(false, false, message);
    }
}
