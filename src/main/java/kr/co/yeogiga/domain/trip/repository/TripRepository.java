package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
