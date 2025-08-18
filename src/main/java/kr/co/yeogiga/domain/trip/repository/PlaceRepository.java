package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
