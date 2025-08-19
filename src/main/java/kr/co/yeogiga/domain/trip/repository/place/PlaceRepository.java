package kr.co.yeogiga.domain.trip.repository.place;

import kr.co.yeogiga.domain.trip.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long>, CustomPlaceRepository {
    int countByTripDay_Id(Long tripDayId);
}
