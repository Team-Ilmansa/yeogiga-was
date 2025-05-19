package kr.co.yeogiga.application.calendar.service;

import kr.co.yeogiga.application.calendar.dto.CalendarRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.calendar.entity.Calendar;
import kr.co.yeogiga.domain.calendar.service.CalendarService;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarQueryService {
    private final CalendarService calendarService;

    /**
     * 특정 사용자의 특정 여행에 대한 가능한 날짜 정보 조회 메서드
     *
     * @param userId 조회 대상 사용자 ID
     * @param tripId 여행 ID
     * @return 해당 사용자의 가능 날짜 정보 DTO
     * @throws CustomException TripErrorType.CALENDAR_NOT_FOUND - 등록된 정보가 없을 경우
     */
    @Transactional(readOnly = true)
    public CalendarRes.UserAvailability getUserAvailability(Long userId, Long tripId) {
        Calendar calendar = calendarService.readByUserIdAndTripId(userId, tripId)
                .orElseThrow(() -> new CustomException(TripErrorType.CALENDAR_NOT_FOUND));

        return CalendarRes.UserAvailability.from(calendar);
    }

    /**
     * 특정 여행(tripId)에 등록된 모든 사용자들의 가능 날짜 정보 조회 메서드
     *
     * @param tripId 여행 ID
     * @return 전체 사용자의 가능 날짜 리스트 DTO
     */
    @Transactional(readOnly = true)
    public CalendarRes.TripAvailabilityList getTripAvailabilities(Long tripId) {
        List<Calendar> calendars = calendarService.readAllByTripId(tripId);
        return CalendarRes.TripAvailabilityList.from(calendars);
    }
}
