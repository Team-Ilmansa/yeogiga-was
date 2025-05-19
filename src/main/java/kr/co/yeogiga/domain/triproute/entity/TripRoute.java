package kr.co.yeogiga.domain.triproute.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.triproute.converter.RouteListConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "trip_route")
public class TripRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int day;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = RouteListConverter.class)
    private List<Route> routes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Builder
    public TripRoute(int day, List<Route> routes, Trip trip) {
        this.day = day;
        this.routes = routes;
        this.trip = trip;
    }
}
