package kr.co.yeogiga.domain.tripplace.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collation = "trip_day_place")
public class TripDayPlace {
    @Id
    private String id;
    private Long tripId;
    private int day;
    private LocalDate date;
    private List<Place> places;

    @Builder
    public TripDayPlace(Long tripId, int day, LocalDate date, List<Place> places) {
        this.tripId = tripId;
        this.day = day;
        this.date = date;
        this.places = places;
    }
}
