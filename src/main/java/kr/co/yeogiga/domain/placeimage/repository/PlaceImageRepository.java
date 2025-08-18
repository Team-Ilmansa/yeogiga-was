package kr.co.yeogiga.domain.placeimage.repository;

import kr.co.yeogiga.domain.placeimage.entity.PlaceImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaceImageRepository extends MongoRepository<PlaceImage, String> {
}
