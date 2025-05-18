package kr.co.yeogiga.application.calendar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.yeogiga.domain.calendar.entity.Calendar;
import kr.co.yeogiga.domain.trip.entity.Trip;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "CalendarReq", description = "W2M 쓰기 연산 DTO")
public record CalendarReq(
        @Schema(description = "가능한 날짜 리스트", example = "[\"2025-07-01\", \"2025-07-02\", \"2025-07-10\"]")
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
