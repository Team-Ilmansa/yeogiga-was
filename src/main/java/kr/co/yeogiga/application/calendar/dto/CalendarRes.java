package kr.co.yeogiga.application.calendar.dto;

import kr.co.yeogiga.domain.calendar.entity.Calendar;

import java.time.LocalDate;
import java.util.List;

public class CalendarRes {

    public record UserAvailability(
            Long userId,
            List<LocalDate> availableDates
    ) {
        public static UserAvailability from(Calendar calendar) {
            return new UserAvailability(calendar.getUserId(), calendar.getAvailableDates());
        }
    }

    public record TripAvailabilityList(
            List<UserAvailability> availabilities
    ) {
        public static TripAvailabilityList from(List<Calendar> calendars) {
            return new TripAvailabilityList(
                    calendars.stream().map(UserAvailability::from).toList()
            );
        }
    }
}
