package kr.co.yeogiga.domain.calendar.service;

import kr.co.yeogiga.domain.calendar.entity.Calendar;
import kr.co.yeogiga.domain.calendar.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarRepository calendarRepository;

    public void save(Calendar calendar) {
        calendarRepository.save(calendar);
    }

    public Optional<Calendar> readByUserIdAndTripId(Long userId, Long tripId) {
        return calendarRepository.findByUserIdAndTrip_Id(userId, tripId);
    }

    public List<Calendar> readAllByTripId(Long tripId) {
        return calendarRepository.findAllByTrip_Id(tripId);
    }

    public boolean existsByUserIdAndTripId(Long userId, Long tripId) {
        return calendarRepository.existsByUserIdAndTrip_Id(userId, tripId);
    }
}
