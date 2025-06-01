package kr.co.yeogiga.application.pin.service;

import kr.co.yeogiga.application.fcm.service.TripPushSender;
import kr.co.yeogiga.application.pin.dto.PinReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.domain.trip.dto.TripFcmTokenInfoDto;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PinConstant;
import kr.co.yeogiga.infrastructure.redis.constant.TripMemberTokenConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PinCommandService {
    private final TripService tripService;
    private final RedisRepository redisRepository;
    private final TripPushSender tripPushSender;

    /**
     * 집결지 핀 생성 요청 메서드
     *
     * @param tripId    여행 ID
     * @param pin       핀 생성 요청 DTO
     */
    @Transactional(readOnly = true)
    public void createPin(Long tripId, PinReq.Creation pin) {
        if (!tripService.existsById(tripId)) {
            throw new CustomException(TripErrorType.TRIP_NOT_FOUND);
        }

        String pinKey = PinConstant.pinKey(tripId);
        redisRepository.set(pinKey, pin.toEntity(), calculatePinDuration(pin.time()));

        // Push 알림 전송
        sendPinPush(tripId, pin);
    }

    /**
     * 집결지 핀의 만료 기한(TTL)을 계산하는 메서드
     *
     * @param time          사용자가 요청한 집결지 시간
     * @return              집결지 핀 만료 기한
     */
    private Duration calculatePinDuration(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();

        if (time.isBefore(now)) {
            throw new CustomException(CommonErrorType.TIME_SHOULD_NOT_BEFORE_NOW);
        }

        return Duration.between(LocalDateTime.now(), time);
    }

    /**
     * 집결지(Pin) 생성 후 Push 알림을 보내는 메서드
     *
     * @param tripId    알림 대상 여행 ID
     * @param pin       집결지 정보
     */
    private void sendPinPush(Long tripId, PinReq.Creation pin) {
        List<TripFcmTokenInfoDto> fcmTokenInfos = tripService.readTripFcmTokenInfosById(tripId);

        if (fcmTokenInfos.isEmpty()) return;

        List<String> fcmTokens = fcmTokenInfos.stream()
                .map(TripFcmTokenInfoDto::fcmToken)
                .toList();

        String title = fcmTokenInfos.get(0).title();

        String redisKey = TripMemberTokenConstant.tripTokenKey(tripId);

        tripPushSender.sendPinPush(tripId, title, pin.toEntity(), redisKey, fcmTokens);
    }
}
