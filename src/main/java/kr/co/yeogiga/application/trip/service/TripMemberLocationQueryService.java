package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripMemberLocationDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.TripMemberLocationConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripMemberLocationQueryService {
    private final RedisRepository redisRepository;
    private final TripMemberService tripMemberService;
    private final UserService userService;
    
    /**
     * 특정 여행에 포함된 사용자의 위치를 조회하는 메서드
     *
     * @param tripId        여행 ID
     * @param userId        사용자 ID
     * @return              여행에 포함된 사용자(멤버)의 위치 정보 DTO
     */
    @Transactional(readOnly = true)
    public List<TripMemberLocationDto.Response> readMemberLocations(Long tripId, Long userId) {
        if (!tripMemberService.existsByTripIdAndUserId(tripId, userId)) {
            throw new CustomException(TripMemberErrorType.IS_NOT_MEMBER);
        }
        
        String key = TripMemberLocationConstant.tripMemberLocationKey(tripId);
        
        Set<String> subKeys = redisRepository.getHashKeys(key);
        
        List<TripMemberLocationDto.StoredFormat> memberLocations
                = redisRepository.getHashAll(key, subKeys, TripMemberLocationDto.StoredFormat.class);
        
        return generateLocationResponse(memberLocations);
    }
    
    /**
     * 위치 정보 응답 DTO를 생성하는 메서드
     * - 사용자 위치 저장 정보(StoredFormat)로부터 응답(Response) 정보 DTO를 생성
     *
     * @param memberLocations       사용자 위치 저장 정보 목록
     * @return                      사용자 위치 응답 정보 목록
     */
    private List<TripMemberLocationDto.Response> generateLocationResponse(
            List<TripMemberLocationDto.StoredFormat> memberLocations
    ) {
       List<Long> userIds = memberLocations.stream()
               .map(TripMemberLocationDto.StoredFormat::userId)
               .toList();
       
       Map<Long, User> users = userService.readAllByIds(userIds).stream()
               .collect(Collectors.toMap(User::getId, user -> user));
       
       return memberLocations.stream()
               .map(locationInfo -> {
                   User user = users.get(locationInfo.userId());
                
                   return TripMemberLocationDto.Response.from(locationInfo, user);
               }).toList();
    }
}
