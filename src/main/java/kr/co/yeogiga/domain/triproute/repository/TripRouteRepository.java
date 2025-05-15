package kr.co.yeogiga.domain.triproute.repository;

import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRouteRepository extends JpaRepository<TripRoute, Long>, CustomTripRouteRepository {
}
