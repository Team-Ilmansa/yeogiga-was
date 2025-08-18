package kr.co.yeogiga.domain.trip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "is_visited")
    private boolean isVisited;

    @Column(name = "place_type")
    @Enumerated(EnumType.STRING)
    private PlaceCategory placeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_day_id")
    private TripDay tripDay;

    @Builder
    public Place(String name, Double latitude, Double longitude,
                 PlaceCategory placeType, int sortOrder, TripDay tripDay) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sortOrder = sortOrder;
        this.isVisited = false;
        this.placeType = placeType;
        this.tripDay = tripDay;
    }
}
