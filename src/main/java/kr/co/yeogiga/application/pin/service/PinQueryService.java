package kr.co.yeogiga.application.pin.service;

import kr.co.yeogiga.domain.pin.entity.Pin;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.PinConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PinQueryService {
    private final RedisRepository redisRepository;

    /**
     * 집결지 핀 조회 메서드
     *
     * @param tripId    여행 ID
     * @return          집결지 핀
     */
    public Pin getPin(Long tripId) {
        return (Pin) redisRepository.get(PinConstant.pinKey(tripId));
    }
}
