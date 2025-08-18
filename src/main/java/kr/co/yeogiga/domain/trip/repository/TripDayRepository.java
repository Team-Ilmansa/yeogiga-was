package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripDayRepository extends JpaRepository<TripDay, Long> {
}
