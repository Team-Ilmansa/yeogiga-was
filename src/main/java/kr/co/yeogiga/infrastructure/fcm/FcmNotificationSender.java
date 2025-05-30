package kr.co.yeogiga.infrastructure.fcm;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import kr.co.yeogiga.infrastructure.fcm.response.FcmSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class FcmNotificationSender {

    /**
     * Silent Push 메서드
     */
    public FcmSendResult sendSilent(String token, Map<String, String> data) {
        Message message = buildSilentMessage(token, data);
        return send(token, message);
    }

    /**
     * 일반 Notification Push 메서드
     */
    public FcmSendResult sendNotification(String token, String title, String body, Map<String, String> data) {
        Message message = buildNotificationMessage(token, title, body, data);
        return send(token, message);
    }

    /**
     * FCM 푸시 알림을 전송하고 결과를 반환하는 메서드
     *
     * @param token   수신자의 FCM 디바이스 토큰
     * @param message fcm message 객체
     * @return FCM 전송 결과 객체
     */
    private FcmSendResult send(String token, Message message) {
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return FcmSendResult.success(response);
        } catch (FirebaseMessagingException e) {
            MessagingErrorCode code = e.getMessagingErrorCode();

            if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                log.info("[FCM] Detected invalid token to be removed: {}", token);
                return FcmSendResult.invalidToken(e.getMessage());
            }

            log.error("[FCM] Failed to send push notification: {}", e.getMessage(), e);
            return FcmSendResult.failure(e.getMessage());
        }
    }

    /**
     * Silent Push를 위한 메시지 생성 메서드
     *
     * @param token 수신자의 FCM 디바이스 토큰
     * @param data  fcm data 정보
     * @return FCM 전송 메시지
     */
    private Message buildSilentMessage(String token, Map<String, String> data) {
        return Message.builder()
                .setToken(token)
                .putAllData(data)
                .setApnsConfig(ApnsConfig.builder()
                        .putHeader("apns-priority", "5")
                        .putHeader("apns-push-type", "background")
                        .setAps(Aps.builder()
                                .setContentAvailable(true)
                                .build())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build())
                .build();
    }

    /**
     * 일반 Push를 위한 메시지 생성 메서드
     *
     * @param token 수신자의 FCM 디바이스 토큰
     * @param title 메시지 제목
     * @param body  메시지 내용
     * @param data  fcm data 정보
     * @return FCM 전송 메시지
     */
    private Message buildNotificationMessage(String token, String title, String body, Map<String, String> data) {
        return Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setSound("default").build())
                        .build())
                .build();
    }
}

