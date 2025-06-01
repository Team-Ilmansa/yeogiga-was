package kr.co.yeogiga.application.fcm.service;

import kr.co.yeogiga.application.fcm.constant.FcmConstant;
import kr.co.yeogiga.application.user.service.UserFcmTokenService;
import kr.co.yeogiga.domain.pin.entity.Pin;
import kr.co.yeogiga.infrastructure.fcm.FcmNotificationSender;
import kr.co.yeogiga.infrastructure.fcm.response.FcmSendResult;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TripPushSender {
    private final FcmNotificationSender fcmNotificationSender;
    private final UserFcmTokenService userFcmTokenService;
    private final RedisRepository redisRepository;

    /**
     * FCM 토큰을 통해 Silent Push를 비동기로 전송하는 메서드
     * - 유효하지 않은 토큰은 Redis 및 RDB에서 제거
     *
     * @param tripId       알림 대상 여행 ID
     * @param redisListKey FCM 토큰이 저장된 Redis 리스트 키
     * @param fcmTokens    푸시 알림 전송 대상 FCM 토큰 List
     */
    @Async
    public void sendPush(Long tripId, String redisListKey, List<String> fcmTokens) {
        Map<String, String> notificationData = buildTripPushData(tripId);

        for (String fcmToken : fcmTokens) {
            FcmSendResult result = fcmNotificationSender.sendSilent(fcmToken, notificationData);

            if (result.shouldRemoveToken()) {
                deleteFcmToken(redisListKey, fcmToken);
            }
        }
    }

    /**
     * FCM 토큰을 통해 Notification Push를 비동기로 전송하는 메서드
     * - 유효하지 않은 토큰은 Redis 및 RDB에서 제거
     *
     * @param tripId       알림 대상 여행 ID
     * @param title        알림 대상 여행 제목
     * @param redisListKey FCM 토큰이 저장된 Redis 리스트 키
     * @param fcmTokens    푸시 알림 전송 대상 FCM 토큰 List
     */
    @Async
    public void sendPush(Long tripId, String title, String redisListKey, List<String> fcmTokens) {
        Map<String, String> notificationData = buildTripPushData(tripId);
        String pushTitle = FcmConstant.formatTitle(title);
        String body = FcmConstant.FCM_BODY;

        for (String fcmToken : fcmTokens) {
            FcmSendResult result = fcmNotificationSender.sendNotification(fcmToken, pushTitle, body, notificationData);

            if (result.shouldRemoveToken()) {
                deleteFcmToken(redisListKey, fcmToken);
            }
        }
    }

    /**
     * 유효하지 않은 FCM Token을 삭제하는 메서드
     * - Redis 및 RDB에 저장된 FCM Token 정보를 삭제
     *
     * @param redisListKey FCM 토큰이 저장된 Redis 리스트 키
     * @param fcmToken     삭제할 FCM 토큰
     */
    private void deleteFcmToken(String redisListKey, String fcmToken) {
        redisRepository.removeFromList(redisListKey, fcmToken);
        userFcmTokenService.deleteFcmTokenByToken(fcmToken);
    }

    /**
     * Push data 생성 메서드
     *
     * @param tripId 알림 대상 여행 ID
     * @return data가 저장된 Map 자료구조
     */
    private Map<String, String> buildTripPushData(Long tripId) {
        Map<String, String> data = new HashMap<>();
        data.put("tripId", tripId.toString());
        return data;
    }
}
