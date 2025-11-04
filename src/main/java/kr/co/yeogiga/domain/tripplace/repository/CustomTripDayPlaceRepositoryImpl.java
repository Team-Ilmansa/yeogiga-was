package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.tripplace.dto.ImagesPlaceDto;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomTripDayPlaceRepositoryImpl implements CustomTripDayPlaceRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public void savePlace(String id, Place place) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().push("places", place);
        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public void saveImage(String id, String placeId, Image image) {
        Query query = Query.query(
                Criteria.where("_id").is(id)
                        .and("places.id").is(placeId)
        );
        Update update = new Update().push("places.$.images", image);
        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public void saveImageToUnmatched(String id, Image image) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().push("unmatchedImages", image);
        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public Double findMaxOrderById(String id) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                Aggregation.unwind("places"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "places.order")),
                Aggregation.limit(1),
                Aggregation.project().and("places.order").as("order")
        );

        Document doc = mongoTemplate.aggregate(aggregation, "trip_day_place", Document.class)
                .getUniqueMappedResult();

        return doc != null ? doc.getDouble("order") : 0.0;
    }

    @Override
    public Optional<TripDayPlace> findByIdSorted(String id) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),

                Aggregation.unwind("places", true),

                Aggregation.project("tripId", "day")
                        .and("places.id").as("places.id")
                        .and("places.name").as("places.name")
                        .and("places.latitude").as("places.latitude")
                        .and("places.longitude").as("places.longitude")
                        .and("places.placeType").as("places.placeType")
                        .and("places.order").as("places.order")
                        .and("places.isVisited").as("places.isVisited"),

                Aggregation.sort(Sort.by(Sort.Direction.ASC, "places.order")),

                Aggregation.group("_id")
                        .first("tripId").as("tripId")
                        .first("day").as("day")
                        .push("places").as("places")
        );

        AggregationResults<TripDayPlace> results =
                mongoTemplate.aggregate(aggregation, "trip_day_place", TripDayPlace.class);

        return results.getMappedResults().stream().findFirst();
    }

    @Override
    public List<TripDayPlace> findByTripIdSorted(Long tripId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("tripId").is(tripId)),

                Aggregation.unwind("places", true),

                Aggregation.project("tripId", "day")
                        .and("places.id").as("places.id")
                        .and("places.name").as("places.name")
                        .and("places.placeType").as("places.placeType")
                        .and("places.order").as("places.order")
                        .and("places.isVisited").as("places.isVisited"),

                Aggregation.sort(Sort.by(Sort.Direction.ASC, "places.order")),

                Aggregation.group("_id")
                        .first("tripId").as("tripId")
                        .first("day").as("day")
                        .push("places").as("places"),

                Aggregation.sort(Sort.by(Sort.Direction.ASC, "day"))
        );


        AggregationResults<TripDayPlace> results =
                mongoTemplate.aggregate(aggregation, "trip_day_place", TripDayPlace.class);

        return results.getMappedResults();
    }

    @Override
    public List<Image> findAllImagesByTripIdAndDay(Long tripId, int day) {
        Criteria criteria = Criteria.where("tripId").is(tripId)
                .and("day").is(day);

        Query query = new Query(criteria);
        query.fields().include("unmatchedImages").include("places.images");

        TripDayPlace doc = mongoTemplate.findOne(query, TripDayPlace.class);
        if (doc == null) return Collections.emptyList();

        List<Image> images = new ArrayList<>();

        images.addAll(doc.getUnmatchedImages());

        for (Place p : doc.getPlaces()) {
            images.addAll(p.getImages());
        }

        return images;
    }

    @Override
    public ImagesPlaceDto.Response findImagesGroupedByPlace(Long tripId, int day) {
        Criteria criteria = Criteria.where("tripId").is(tripId)
                .and("day").is(day);

        Query query = new Query(criteria);
        query.fields()
                .include("unmatchedImages")
                .include("places.images")
                .include("places.id");

        TripDayPlace doc = mongoTemplate.findOne(query, TripDayPlace.class);
        if (doc == null) {
            return new ImagesPlaceDto.Response(List.of(), List.of());
        }

        List<Image> unmatched = Optional.ofNullable(doc.getUnmatchedImages())
                .map(List::copyOf)
                .orElseGet(List::of);

        List<ImagesPlaceDto.PlaceImages> byPlace = new ArrayList<>();
        List<Place> places = Optional.ofNullable(doc.getPlaces()).orElseGet(List::of);

        for (Place p : places) {
            if (p.getId() == null) continue;
            List<Image> imgs = Optional.ofNullable(p.getImages())
                    .map(List::copyOf)
                    .orElseGet(List::of);
            byPlace.add(new ImagesPlaceDto.PlaceImages(p.getId(), imgs));
        }

        return new ImagesPlaceDto.Response(byPlace, unmatched);
    }

    @Override
    public Optional<Place> findPlaceByIdAndPlaceId(String id, String placeId) {
        Criteria criteria = Criteria.where("_id").is(id)
                .and("places.id").is(placeId);

        Query query = new Query(criteria);
        query.fields().include("places.$");

        TripDayPlace result = mongoTemplate.findOne(query, TripDayPlace.class);

        if (result == null || result.getPlaces() == null || result.getPlaces().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.getPlaces().get(0));
    }

    @Override
    public List<Image> findUnmatchedImagesById(String id) {
        Criteria criteria = Criteria.where("_id").is(id);

        Query query = new Query(criteria);
        query.fields().include("unmatchedImages");

        TripDayPlace result = mongoTemplate.findOne(query, TripDayPlace.class);

        if (result == null || result.getUnmatchedImages() == null) {
            return List.of();
        }

        return result.getUnmatchedImages();
    }

    @Override
    public List<TripDayPlace> findTripDayPlaceSummariesByTripId(Long tripId) {
        Aggregation aggregation = Aggregation.newAggregation(
                // 해당 tripId에 해당하는 모든 TripDayPlace 조회
                Aggregation.match(Criteria.where("tripId").is(tripId)),

                // unmatchedImages 1장만
                Aggregation.project("day")
                        .and("_id").as("id")
                        .and("places").as("places")
                        .and(ArrayOperators.Slice.sliceArrayOf("unmatchedImages").itemCount(1)).as("unmatchedImage"),

                // places 펼침 (null값 포함)
                Aggregation.unwind("places", true),

                // place 필드 정제 + 이미지 1장만
                Aggregation.project("id", "day", "unmatchedImage")
                        .and("places.id").as("placeId")
                        .and("places.name").as("name")
                        .and("places.latitude").as("latitude")
                        .and("places.longitude").as("longitude")
                        .and("places.placeType").as("placeType")
                        .and("places.order").as("order")
                        .and(ArrayOperators.Slice.sliceArrayOf("places.images").itemCount(1)).as("image"),

                // order 정렬
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "order")),

                // 다시 TripDayPlace 단위로 그룹화
                Aggregation.group("_id")
                        .first("day").as("day")
                        .first("unmatchedImage").as("unmatchedImages")
                        .push(
                                new Document("id", "$placeId")
                                        .append("name", "$name")
                                        .append("latitude", "$latitude")
                                        .append("longitude", "$longitude")
                                        .append("placeType", "$placeType")
                                        .append("order", "$order")
                                        .append("images", "$image")
                        ).as("places"),

                // day 기준 정렬
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "day"))
        );

        AggregationResults<TripDayPlace> result = mongoTemplate.aggregate(
                aggregation, "trip_day_place", TripDayPlace.class
        );

        return result.getMappedResults();
    }

    @Override
    public List<Place> findAllPlacesByTripIdAndDay(Long tripId, int day) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("tripId").is(tripId).and("day").is(day)),

                Aggregation.unwind("places"),

                Aggregation.sort(Sort.by(Sort.Direction.ASC, "places.order")),

                Aggregation.project()
                        .and("places.id").as("id")
                        .and("places.name").as("name")
                        .and("places.latitude").as("latitude")
                        .and("places.longitude").as("longitude")
                        .and("places.placeType").as("placeType")
                        .and("places.order").as("order")
                        .and("places.isVisited").as("isVisited")
        );

        AggregationResults<Place> result = mongoTemplate.aggregate(aggregation, "trip_day_place", Place.class);

        return result.getMappedResults();
    }

    @Override
    public List<Image> findFavoriteImages(String id) {
        // 목적지에 매핑된 이미지 중 favorite == true
        Aggregation placesImages = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                Aggregation.unwind("places"),
                Aggregation.unwind("places.images"),
                Aggregation.match(Criteria.where("places.images.favorite").is(true)),
                Aggregation.project()
                        .and("places.images._id").as("_id")
                        .and("places.images.url").as("url")
                        .and("places.images.latitude").as("latitude")
                        .and("places.images.longitude").as("longitude")
                        .and("places.images.date").as("date")
                        .and("places.images.favorite").as("favorite")
        );

        List<Image> favoriteFromPlaces = mongoTemplate
                .aggregate(placesImages, "trip_day_place", Image.class)
                .getMappedResults();

        // 2. unmatchedImages 중 favorite == true
        Aggregation unmatchedImages = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                Aggregation.unwind("unmatchedImages"),
                Aggregation.match(Criteria.where("unmatchedImages.favorite").is(true)),
                Aggregation.project()
                        .and("unmatchedImages._id").as("_id")
                        .and("unmatchedImages.url").as("url")
                        .and("unmatchedImages.latitude").as("latitude")
                        .and("unmatchedImages.longitude").as("longitude")
                        .and("unmatchedImages.date").as("date")
                        .and("unmatchedImages.favorite").as("favorite")
        );

        List<Image> favoriteFromUnmatched = mongoTemplate
                .aggregate(unmatchedImages, "trip_day_place", Image.class)
                .getMappedResults();

        return List.of(favoriteFromPlaces, favoriteFromUnmatched).stream()
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public void updateImageFavorite(String id, String placeId, String imageId, boolean favorite) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update;

        if (placeId != null) {
            update = new Update()
                    .set("places.$[p].images.$[i].favorite", favorite)
                    .filterArray(Criteria.where("p.id").is(placeId))
                    .filterArray(Criteria.where("i._id").is(imageId));
        } else {
            update = new Update()
                    .set("unmatchedImages.$[i].favorite", favorite)
                    .filterArray(Criteria.where("i._id").is(imageId));
        }

        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public void updatePlaceVisited(String id, String placeId, boolean isVisited) {
        Query query = Query.query(
                Criteria.where("_id").is(id)
                        .and("places.id").is(placeId)
        );
        Update update = new Update().set("places.$.isVisited", isVisited);
        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public void deletePlace(String id, String placeId) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().pull("places", Query.query(Criteria.where("id").is(placeId)));
        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public void deleteImage(String id, String placeId, String imageId) {
        Query query = new Query(
                Criteria.where("_id").is(id)
                        .and("places.id").is(placeId)
        );

        Update update = new Update()
                .pull("places.$.images", Query.query(Criteria.where("id").is(imageId)));
        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public void deleteImageFromUnMatched(String id, String imageId) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().pull("unmatchedImages", Query.query(Criteria.where("id").is(imageId)));
        mongoTemplate.updateFirst(query, update, TripDayPlace.class);
    }

    @Override
    public void deleteImagesByTripId(Long tripId, List<String> imageIds) {
        Query query = Query.query(Criteria.where("tripId").is(tripId));

        Update update = new Update()
                .pull("unmatchedImages", Query.query(Criteria.where("id").in(imageIds)))
                .pull("places.$[].images", Query.query(Criteria.where("id").in(imageIds)));

        mongoTemplate.updateMulti(query, update, TripDayPlace.class);
    }
}
