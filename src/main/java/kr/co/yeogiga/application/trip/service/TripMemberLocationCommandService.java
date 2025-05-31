package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripMemberLocationDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.TripMemberLocationConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TripMemberLocationCommandService {
    private final TripMemberService tripMemberService;
    private final RedisRepository redisRepository;

    /**
     * 사용자의 현재 위치를 Redis에 저장하는 메서드
     * - Redis의 Hash 자료구조를 통한 저장
     * - 각 필드(사용자 위치 정보)마다 30분의 ttl을 설정
     *
     * @param tripId        여행 ID
     * @param userId        사용자 ID
     * @param location      사용자 위치 정보 DTO
     */
    public void saveLocation(Long tripId, Long userId, TripMemberLocationDto.Request location) {
        if (!tripMemberService.existsByTripIdAndUserId(tripId, userId)) {
            throw new CustomException(TripMemberErrorType.IS_NOT_MEMBER);
        }

        String key = TripMemberLocationConstant.tripMemberLocationKey(tripId);
        String subKey = TripMemberLocationConstant.tripMemberLocationSubKey(userId);

        redisRepository.setHash(key, subKey, location.toStoredFormat(userId));
        redisRepository.setHashExpire(key, subKey, Duration.ofMinutes(30));
    }
}
