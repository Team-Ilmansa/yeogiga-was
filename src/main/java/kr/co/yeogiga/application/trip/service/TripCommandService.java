package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.fcm.service.TripPushSender;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.dto.TripFcmTokenQueryDto;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import kr.co.yeogiga.infrastructure.redis.constant.TripMemberTokenConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripCommandService {
    private final TripService tripService;
    private final TripMemberService tripMemberService;
    private final UserService userService;
    private final RedisRepository redisRepository;
    private final TripPushSender tripPushSender;

    /**
     * 여행 생성 메서드
     *
     * @param leaderId          방장(여행 생성자) ID
     * @param creationRequest   여행 생성 요청 DTO
     */
    @Transactional
    public Long create(Long leaderId, TripReq.Creation creationRequest) {
        Trip trip = Trip.builder()
                .title(creationRequest.title())
                .leaderId(leaderId)
                .travelStatus(TravelStatus.SETTING)
                .build();

        User leader = userService.readById(leaderId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        TripMember tripMember = TripMember.builder()
                .trip(trip)
                .user(leader)
                .build();


        tripMemberService.save(tripMember);
        return tripService.save(trip).getId();
    }

    /**
     * 여행 시간 수정 메서드
     * - 여행 일정과 목적지까지 확정이 나지 않은 상태(SETTING)일 경우, 여행 상태(TravelStatus)값 미변경
     *
     * @param tripId        여행 ID
     * @param userId        요청자 ID
     * @param time          여행 시간 수정 요청 DTO
     *
     * @throws CustomException  TripErrorType.TRIP_NOT_FOUND - 여행 조회 불가
     * @throws CustomException  TripErrorType.INVALID_DATE_RANGE - 여행 시간 범위 오류 (종료 시각 <= 출발시각)
     * @throws CustomException  TripErrorType.PERMISSION_DENIED_NOT_LEADER - 방장이 아닌 사용자
     */
    @Transactional
    public void updateTime(Long tripId, Long userId, TripReq.Time time) {
        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        if (!time.isValid()) {
            throw new CustomException(TripErrorType.INVALID_DATE_RANGE);
        }

        if (!trip.getLeaderId().equals(userId)) {
            throw new CustomException(TripErrorType.PERMISSION_DENIED_NOT_LEADER);
        }

        if (trip.getTravelStatus() != TravelStatus.SETTING) {
            TravelStatus status = TravelStatus.resolveStatus(time.start(), time.end());
            trip.updateStatus(status);
        }

        trip.updateTime(time.start(), time.end());
    }

    /**
     * 여행 상태 갱신 메서드
     * 1. 진행 상태로 변화하는 여행 멤버의 Fcm Token을 Redis에 저장
     * 2. 조건에 만족하는 여행 상태 데이터 변경
     *
     * @param time      현재 시간
     */
    @Transactional
    public void updateTravelStatus(LocalDateTime time) {
        List<TripFcmTokenQueryDto> tripFcmTokens = tripService.readTripFcmTokensByTime(time);

        // FCM Token 저장 로직
        if (!tripFcmTokens.isEmpty()) {
            cacheTripFcmTokensToRedis(tripFcmTokens);
            tripService.updateAllTravelStatusToInProgress(time);
        }

        tripService.updateAllTravelStatusToCompleted(time);
    }

    /**
     * 여행 ID별로 그룹화된 FCM 토큰 목록을 Redis에 저장하는 메서드
     *
     * @param tripFcmTokens 시간 조건에 맞는 여행 FCM 토큰 목록
     */
    private void cacheTripFcmTokensToRedis(List<TripFcmTokenQueryDto> tripFcmTokens) {
        Map<Long, List<TripFcmTokenQueryDto>> grouped = tripFcmTokens.stream()
                .collect(Collectors.groupingBy(TripFcmTokenQueryDto::tripId));

        for (Map.Entry<Long, List<TripFcmTokenQueryDto>> entry : grouped.entrySet()) {
            Long tripId = entry.getKey();
            List<TripFcmTokenQueryDto> dtos = entry.getValue();

            if (dtos.isEmpty()) continue;

            saveTripFcmTokensToRedis(tripId, dtos);
        }
    }

    /**
     * 특정 여행 ID에 대한 FCM 토큰 리스트를 Redis에 저장하고 만료 시간 설정 메서드
     *
     * @param tripId    여행 ID
     * @param dtos      해당 여행의 FCM 토큰 및 종료 시간 정보
     */
    private void saveTripFcmTokensToRedis(Long tripId, List<TripFcmTokenQueryDto> dtos) {
        String redisKey = TripMemberTokenConstant.tripTokenKey(tripId);

        // 토큰 리스트 추출
        List<String> tokens = dtos.stream()
                .map(TripFcmTokenQueryDto::fcmToken)
                .filter(Objects::nonNull)
                .toList();

        // 여행 종료 시간
        LocalDateTime endedAt = dtos.get(0).endedAt();

        String title = dtos.get(0).title();

        // FCM Token Redis Im-Memory 저장 및 TTL 설정
        redisRepository.setListAll(redisKey, tokens);
        redisRepository.expire(redisKey, calculateDuration(endedAt));

        // Push 알림 전송
        tripPushSender.sendPush(tripId, title, redisKey, tokens);
    }

    /**
     * Redis 만료 기한(TTL)을 계산하는 메서드
     *
     * @param time          여행 종료 시간
     * @return              redis 만료 기한
     */
    private Duration calculateDuration(LocalDateTime time) {
        return Duration.between(LocalDateTime.now(), time);
    }

    /**
     * 여행 정보를 갱신하는 메서드
     * - 여행 제목 갱신
     *
     * @param tripId            여행 ID
     * @param updateRequest     여행 업데이터 요청 DTO (title)
     */
    @Transactional
    public void updateTripInfo(Long tripId, TripReq.Update updateRequest) {
        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        if (trip.getTitle().equals(updateRequest.title())) {
            throw new CustomException(TripErrorType.SAME_TRIP_TITLE);
        }

        trip.updateInfo(updateRequest.title());
    }

    /**
     * 여행 삭제 메서드
     *
     * @param tripId    여행 ID
     */
    public void removeTrip(Long tripId) {
        tripService.deleteById(tripId);
    }
}
