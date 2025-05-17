package kr.co.yeogiga.domain.triproute.repository;

import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRouteRepository extends JpaRepository<TripRoute, Long>, CustomTripRouteRepository {
    Optional<TripRoute> findByTripIdAndDay(Long tripId, int day);
    List<TripRoute> findByTripIdOrderByDay(Long tripId);
}
