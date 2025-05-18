package kr.co.yeogiga.application.calendar.service;

import kr.co.yeogiga.application.calendar.dto.CalendarReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.calendar.entity.Calendar;
import kr.co.yeogiga.domain.calendar.service.CalendarService;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarCommandService {
    private final CalendarService calendarService;
    private final TripService tripService;

    /**
     * 사용자의 여행 가능한 날짜를 생성하는 메서드
     * - 하나의 Trip에 대해 한 사용자는 하나의 Calendar만 가질 수 있다.
     *
     * @param userId      로그인한 사용자 ID
     * @param tripId      여행 ID
     * @param calendarReq 사용자가 선택한 가능한 날짜 목록
     * @throws CustomException TripErrorType.CALENDAR_ALREADY_EXISTS - 이미 가능한 날짜 등록
     * @throws CustomException TripErrorType.TRIP_NOT_FOUND - 존재하지 않는 여행
     */
    @Transactional
    public void create(Long userId, Long tripId, CalendarReq calendarReq) {
        if (calendarService.existsByUserIdAndTripId(userId, tripId)) {
            throw new CustomException(TripErrorType.CALENDAR_ALREADY_EXISTS);
        }

        Trip trip = tripService.readById(tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.TRIP_NOT_FOUND));

        calendarService.save(calendarReq.toEntity(userId, trip));
    }

    /**
     * 사용자의 가능한 날짜를 수정하는 메서드
     * - 전달된 날짜 리스트로 기존 날짜를 교체
     *
     * @param userId      로그인한 사용자 ID
     * @param tripId      여행 ID
     * @param calendarReq 새로 선택한 가능한 날짜 목록
     * @throws CustomException TripErrorType.CALENDAR_NOT_FOUND - 존재하지 않는 가능 날짜
     */
    @Transactional
    public void updateAvailableDates(Long userId, Long tripId, CalendarReq calendarReq) {
        Calendar calendar = calendarService.readByUserIdAndTripId(userId, tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.CALENDAR_NOT_FOUND));

        calendar.updateAvailableDates(calendarReq.availableDates());
    }
}
