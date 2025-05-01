package kr.co.yeogiga.domain.tripplace.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "trip_day_place")
public class TripDayPlace {
    @Id
    private String id;
    private Long tripId;
    private int day;
    private List<Place> places;

    @Builder
    public TripDayPlace(Long tripId, int day, List<Place> places) {
        this.tripId = tripId;
        this.day = day;
        this.places = places;
    }

    public void updatePlaces(List<Place> places) {
        this.places = places;
    }
}
