package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TempPlaceImagesRepository extends MongoRepository<TempPlaceImages, String>, CustomTempPlaceImagesRepository {
    Optional<TempPlaceImages> findByTripDayPlaceId(String tripDayPlaceId);
}
