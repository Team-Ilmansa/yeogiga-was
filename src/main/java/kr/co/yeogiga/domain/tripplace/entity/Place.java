package kr.co.yeogiga.domain.tripplace.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Place {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String placeType;
    private double order;
    private LocalDateTime visitedAt;

    @Builder
    public Place(String id, String name, double latitude, double longitude,
                 String placeType, double order, LocalDateTime visitedAt) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeType = placeType;
        this.order = order;
        this.visitedAt = visitedAt;
    }
}
