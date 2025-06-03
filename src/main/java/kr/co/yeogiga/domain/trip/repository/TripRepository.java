package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface TripRepository extends JpaRepository<Trip, Long>, CustomTripRepository {

    List<Trip> findAllByIdIn(Set<Long> ids);
}
