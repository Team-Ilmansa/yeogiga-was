package kr.co.yeogiga.domain.tripplace.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Place {
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String placeType;
    private double order;
    private LocalDateTime visitedAt;
    private List<Image> images;

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
        this.images = new ArrayList<>();
    }

    public void updateOrder(double order) {
        this.order = order;
    }

    public void addImage(Image image) {
        this.images.add(image);
    }
}
