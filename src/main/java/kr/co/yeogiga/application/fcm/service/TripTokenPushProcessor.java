package kr.co.yeogiga.application.fcm.service;

import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.TripMemberTokenConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 진행 중인 여행 멤버들의 GPS정보를 얻기 위해 Silent Push 호출 클래스
 */
@Component
@RequiredArgsConstructor
public class TripTokenPushProcessor {
    private final RedisRepository redisRepository;
    private final TripPushSender tripPushSender;

    /**
     * 진행 중인 여행의 멤버들에게 Silent Push 보내는 메서드
     * - Redis에 저장된 (진행 중인 여행 멤버들) FcmToken을 불러와 전송
     */
    public void process() {
        Set<String> tripTokenKeys = redisRepository.getKeysByPattern(TripMemberTokenConstant.TRIP_MEMBER_TOKEN_KEY_PATTERN);

        if (tripTokenKeys == null || tripTokenKeys.isEmpty()) {
            return;
        }

        for (String redisKey : tripTokenKeys) {
            Long tripId = extractTripIdFromKey(redisKey);
            if (tripId == null) continue;

            sendPushToTripMembers(tripId);
        }
    }

    /**
     * 여행 멤버들에게 보낼 알림 호출 메서드
     * - Redis에 저장된 여행 진행 중인 멤버들의 FcmToken 조회 후 전송
     *
     * @param tripId FCM 푸시 전송 대상 여행 ID
     */
    private void sendPushToTripMembers(Long tripId) {
        String redisListKey = TripMemberTokenConstant.tripTokenKey(tripId);
        List<String> fcmTokens = redisRepository.getList(redisListKey, String.class);

        if (fcmTokens == null || fcmTokens.isEmpty()) {
            return;
        }

        for (String fcmToken : fcmTokens) {
            tripPushSender.sendPush(tripId, redisListKey, fcmToken);
        }
    }

    /**
     * Redis 키 문자열에서 여행 ID(tripId) 추출 메서드
     *
     * @param key Redis 키 (예: "trip:token:{tripId}")
     * @return 추출된 tripId
     */
    private Long extractTripIdFromKey(String key) {
        String[] parts = key.split(":");
        return Long.parseLong(parts[2]);
    }
}
