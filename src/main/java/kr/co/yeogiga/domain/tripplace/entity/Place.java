package kr.co.yeogiga.domain.tripplace.entity;

import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Place {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private PlaceCategory type;
    private double order;
    private LocalDateTime visitedAt;

    @Builder
    public Place(String name, double latitude, double longitude, PlaceCategory type, double order, LocalDateTime visitedAt) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.order = order;
        this.visitedAt = visitedAt;
    }
}
