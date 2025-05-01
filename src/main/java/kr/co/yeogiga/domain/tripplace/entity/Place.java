package kr.co.yeogiga.domain.tripplace.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Place {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String placeType;
    private double order;

    @Builder
    public Place(String id, String name, double latitude,
                 double longitude, String placeType, double order) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeType = placeType;
        this.order = order;
    }

    public void updateOrder(double order) {
        this.order = order;
    }
}
