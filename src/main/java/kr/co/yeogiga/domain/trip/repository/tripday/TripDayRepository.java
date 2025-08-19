package kr.co.yeogiga.domain.trip.repository.tripday;

import kr.co.yeogiga.domain.trip.entity.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripDayRepository extends JpaRepository<TripDay, Long> {
    Optional<TripDay> findByTrip_IdAndDay(Long tripId, int day);
}
