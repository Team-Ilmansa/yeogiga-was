package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@RequiredArgsConstructor
public class CustomTempPlaceImagesRepositoryImpl implements CustomTempPlaceImagesRepository {
    private final MongoTemplate mongoTemplate;

    /**
     * 주어진 placeId에 해당하는 TempPlaceImages 문서에 이미지를 추가하는 쿼리
     * - 문서가 존재하지 않으면 새로 생성함 (upsert)
     * - 기존 문서가 존재하면 images 필드에 새 이미지 객체를 push
     *
     * @param tripDayPlaceId 이미지를 추가할 여행일차 식별자
     * @param image          추가할 이미지 객체
     */
    @Override
    public void saveImage(String tripDayPlaceId, Image image) {
        Query query = new Query(Criteria.where("tripDayPlaceId").is(tripDayPlaceId));
        Update update = new Update().push("images", image);

        mongoTemplate.upsert(query, update, TempPlaceImages.class);
    }

    @Override
    public void deleteImages(String id, List<String> imageIds) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().pull("images", Query.query(Criteria.where("id").in(imageIds)));
        mongoTemplate.updateFirst(query, update, TempPlaceImages.class);
    }
}
