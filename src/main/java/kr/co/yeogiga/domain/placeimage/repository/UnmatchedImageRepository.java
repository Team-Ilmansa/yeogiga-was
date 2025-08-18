package kr.co.yeogiga.domain.placeimage.repository;

import kr.co.yeogiga.domain.placeimage.entity.UnmatchedImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UnmatchedImageRepository extends MongoRepository<UnmatchedImage, String> {
}
