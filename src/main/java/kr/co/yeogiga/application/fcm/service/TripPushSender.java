package kr.co.yeogiga.application.fcm.service;

import kr.co.yeogiga.application.user.service.UserFcmTokenService;
import kr.co.yeogiga.infrastructure.fcm.FcmNotificationSender;
import kr.co.yeogiga.infrastructure.fcm.response.FcmSendResult;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TripPushSender {
    private final FcmNotificationSender fcmNotificationSender;
    private final UserFcmTokenService userFcmTokenService;
    private final RedisRepository redisRepository;

    /**
     * FCM 토큰을 통해 푸시 알림을 비동기로 전송하는 메서드
     * - 유효하지 않은 토큰은 Redis 및 DB에서 제거
     *
     * @param tripId       알림 대상 여행 ID
     * @param redisListKey FCM 토큰이 저장된 Redis 리스트 키
     * @param fcmToken     푸시 알림 전송 대상 FCM 토큰
     */
    @Async
    public void sendPush(Long tripId, String redisListKey, String fcmToken) {
        Map<String, String> notificationData = buildTripPushData(tripId);

        FcmSendResult result = fcmNotificationSender.send(fcmToken, notificationData);

        if (result.shouldRemoveToken()) {
            redisRepository.removeFromList(redisListKey, fcmToken);
            userFcmTokenService.deleteFcmTokenByToken(fcmToken);
        }
    }

    /**
     * Silent Push를 위한 data 생성 메서드
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
