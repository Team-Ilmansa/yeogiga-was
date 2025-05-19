package kr.co.yeogiga.application.calendar.service;

import kr.co.yeogiga.application.calendar.dto.CalendarRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.calendar.entity.Calendar;
import kr.co.yeogiga.domain.calendar.service.CalendarService;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CalendarQueryServiceTest {

    @Mock
    private CalendarService calendarService;

    @InjectMocks
    private CalendarQueryService calendarQueryService;

    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final Long tripId = 1L;

    private final Calendar calendar1 = Calendar.builder()
            .userId(userId1)
            .availableDates(List.of(LocalDate.of(2025, 7, 1)))
            .build();

    private final Calendar calendar2 = Calendar.builder()
            .userId(userId2)
            .availableDates(List.of(LocalDate.of(2025, 7, 1)))
            .build();

    @Nested
    @DisplayName("단일 사용자 일정 조회 테스트")
    class GetUserAvailabilityTest {

        private final Long userId = 1L;

        private final Calendar calendar = Calendar.builder()
                .userId(userId)
                .availableDates(List.of(LocalDate.of(2025, 7, 1)))
                .build();

        @Test
        @DisplayName("성공")
        void success() {
            // given
            given(calendarService.readByUserIdAndTripId(userId, tripId)).willReturn(Optional.of(calendar));

            // when
            CalendarRes.UserAvailability result = calendarQueryService.getUserAvailability(userId, tripId);

            // then
            assertEquals(userId, result.userId());
            assertEquals(calendar.getAvailableDates(), result.availableDates());
        }

        @Test
        @DisplayName("실패 - 캘린더 없음")
        void failCalendarNotFound() {
            // given
            given(calendarService.readByUserIdAndTripId(userId, tripId)).willReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> calendarQueryService.getUserAvailability(userId, tripId));

            // then
            assertEquals(TripErrorType.CALENDAR_NOT_FOUND, exception.getErrorType());
        }
    }

    @Test
    @DisplayName("Trip 전체 사용자 일정 조회")
    void getTripAvailabilitiesTest() {
        // given
        given(calendarService.readAllByTripId(tripId)).willReturn(List.of(calendar1, calendar2));

        // when
        CalendarRes.TripAvailabilityList result = calendarQueryService.getTripAvailabilities(tripId);

        // then
        assertEquals(2, result.availabilities().size());
    }
}
