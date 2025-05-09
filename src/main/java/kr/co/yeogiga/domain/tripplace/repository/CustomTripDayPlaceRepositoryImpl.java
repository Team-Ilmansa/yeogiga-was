package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
    public Double findOrderByIdAndPlaceId(String id, String placeId) {
        Query query = new Query(
                Criteria.where("_id").is(id)
                        .and("places.id").is(placeId)
        );

        query.fields().include("places.$");
        TripDayPlace result = mongoTemplate.findOne(query, TripDayPlace.class);

        if (result == null || result.getPlaces().isEmpty()) {
            throw new RuntimeException("Place not found");
        }

        return result.getPlaces().get(0).getOrder();
    }

    @Override
    public Optional<TripDayPlace> findByIdSorted(String id) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                Aggregation.unwind("places", true),
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
