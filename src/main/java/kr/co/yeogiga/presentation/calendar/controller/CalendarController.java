package kr.co.yeogiga.presentation.calendar.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.calendar.dto.CalendarReq;
import kr.co.yeogiga.application.calendar.service.CalendarCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.presentation.calendar.api.CalendarApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class CalendarController implements CalendarApi {
    private final CalendarCommandService calendarCommandService;

    @Override
    @PostMapping("/{tripId}/calendars")
    public ResponseEntity<?> createCalendar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody CalendarReq calendarReq
    ) {
        calendarCommandService.create(userDetails.getUserId(), tripId, calendarReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @Override
    @PutMapping("/{tripId}/calendars")
    public ResponseEntity<?> updateAvailableDates(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long tripId,
            @Valid @RequestBody CalendarReq calendarReq
    ) {
        calendarCommandService.updateAvailableDates(userDetails.getUserId(), tripId, calendarReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
