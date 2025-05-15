package kr.co.yeogiga.domain.triproute.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "trip_id")
    private Long tripId;

    private int day;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = RouteListConverter.class)
    private List<Route> routes;

    @Builder
    public TripRoute(Long tripId, int day, List<Route> routes) {
        this.tripId = tripId;
        this.day = day;
        this.routes = routes;
    }
}
