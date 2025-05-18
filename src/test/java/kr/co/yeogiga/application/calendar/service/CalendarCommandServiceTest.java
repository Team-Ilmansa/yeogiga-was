package kr.co.yeogiga.application.calendar.service;

import kr.co.yeogiga.application.calendar.dto.CalendarReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.calendar.entity.Calendar;
import kr.co.yeogiga.domain.calendar.service.CalendarService;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CalendarCommandServiceTest {

    @Mock
    private CalendarService calendarService;

    @Mock
    private TripService tripService;

    @InjectMocks
    private CalendarCommandService calendarCommandService;

    private final Long userId = 1L;
    private final Long tripId = 10L;

    @Nested
    @DisplayName("W2M 생성 테스트")
    class CreateCalendar {

        @Test
        @DisplayName("성공")
        void Success() {
            // given
            CalendarReq request = new CalendarReq(List.of(LocalDate.of(2025, 7, 1)));
            Trip trip = mock(Trip.class);

            given(calendarService.existsByUserIdAndTripId(userId, tripId)).willReturn(false);
            given(tripService.readById(tripId)).willReturn(Optional.of(trip));

            // when
            calendarCommandService.create(userId, tripId, request);

            // then
            verify(calendarService, times(1)).save(any());
        }

        @Test
        @DisplayName("실패 - 이미 등록된 Calendar 존재")
        void AlreadyExists() {
            // given
            CalendarReq request = new CalendarReq(List.of(LocalDate.of(2025, 7, 1)));

            given(calendarService.existsByUserIdAndTripId(userId, tripId)).willReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> calendarCommandService.create(userId, tripId, request));

            // then
            assertEquals(TripErrorType.CALENDAR_ALREADY_EXISTS, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - Trip이 존재하지 않음")
        void TripNotFound() {
            // given
            CalendarReq request = new CalendarReq(List.of(LocalDate.of(2025, 7, 1)));

            given(calendarService.existsByUserIdAndTripId(userId, tripId)).willReturn(false);
            given(tripService.readById(tripId)).willReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> calendarCommandService.create(userId, tripId, request));

            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("W2M 수정 테스트")
    class UpdateAvailableDates {

        @Test
        @DisplayName("성공")
        void Success() {
            // given
            CalendarReq request = new CalendarReq(List.of(LocalDate.of(2025, 7, 2)));
            Calendar calendar = mock(Calendar.class);

            given(calendarService.readByUserIdAndTripId(userId, tripId)).willReturn(Optional.of(calendar));

            // when
            calendarCommandService.updateAvailableDates(userId, tripId, request);

            // then
            verify(calendar, times(1)).updateAvailableDates(request.availableDates());
        }

        @Test
        @DisplayName("실패 - 수정 대상 Calendar 없음")
        void NotFoundCalendar() {
            // given
            CalendarReq request = new CalendarReq(List.of(LocalDate.of(2025, 7, 2)));

            given(calendarService.readByUserIdAndTripId(userId, tripId)).willReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> calendarCommandService.updateAvailableDates(userId, tripId, request));

            // then
            assertEquals(TripErrorType.CALENDAR_NOT_FOUND, exception.getErrorType());
        }
    }
}
