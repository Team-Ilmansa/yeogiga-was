package kr.co.yeogiga.application.pin.service;

import kr.co.yeogiga.application.pin.dto.PinReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PinConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PinCommandService {
    private final TripService tripService;
    private final RedisRepository redisRepository;

    /**
     * 집결지 핀 생성 요청 메서드
     *
     * @param tripId    여행 ID
     * @param pin       핀 생성 요청 DTO
     */
    public void createPin(Long tripId, PinReq.Creation pin) {
        if (!tripService.existsById(tripId)) {
            throw new CustomException(TripErrorType.TRIP_NOT_FOUND);
        }

        String pinKey = PinConstant.pinKey(tripId);
        redisRepository.set(pinKey, pin.toEntity(), calculatePinDuration(pin.time()));
    }

    /**
     * 집결지 핀의 만료 기한(TTL)을 계산하는 메서드
     *
     * @param time          사용자가 요청한 집결지 시간
     * @return              집결지 핀 만료 기한
     */
    private Duration calculatePinDuration(LocalDateTime time) {
        return Duration.between(LocalDateTime.now(), time);
    }
}
