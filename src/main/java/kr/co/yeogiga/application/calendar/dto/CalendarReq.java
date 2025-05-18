package kr.co.yeogiga.application.calendar.dto;

import jakarta.validation.constraints.NotEmpty;
import kr.co.yeogiga.domain.calendar.entity.Calendar;
import kr.co.yeogiga.domain.trip.entity.Trip;

import java.time.LocalDate;
import java.util.List;

public record CalendarReq(
        @NotEmpty(message = "가능한 날짜 목록은 비어 있을 수 없습니다.")
        List<LocalDate> availableDates
) {
    public Calendar toEntity(Long userId, Trip trip) {
        return Calendar.builder()
                .userId(userId)
                .trip(trip)
                .availableDates(availableDates)
                .build();
    }
}
