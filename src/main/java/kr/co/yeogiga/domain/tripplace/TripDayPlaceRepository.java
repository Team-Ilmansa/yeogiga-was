package kr.co.yeogiga.domain.tripplace;

import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TripDayPlaceRepository extends MongoRepository<TripDayPlace, String> {
}
