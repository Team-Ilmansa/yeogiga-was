package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TempPlaceImagesRepository extends MongoRepository<TempPlaceImages, String>, CustomTempPlaceImagesRepository {
}
