package kr.co.yeogiga.domain.calendar.repository;

import kr.co.yeogiga.domain.calendar.entity.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    Optional<Calendar> findByUserIdAndTrip_Id(Long userId, Long tripId);
    boolean existsByUserIdAndTrip_Id(Long userId, Long tripId);
}
