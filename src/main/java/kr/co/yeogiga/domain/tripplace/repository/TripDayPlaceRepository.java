package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TripDayPlaceRepository extends MongoRepository<TripDayPlace, String>, CustomTripDayPlaceRepository {
    List<TripDayPlace> findByTripId(Long tripId);
}
