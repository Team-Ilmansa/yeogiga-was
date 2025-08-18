package kr.co.yeogiga.domain.tripplace.entity;

import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Place {
    @Field("id")
    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private PlaceCategory placeType;
    private Double order;
    private List<Image> images;
    private boolean isVisited;

    @Builder
    public Place(String id, String name, Double latitude,
                 Double longitude, PlaceCategory placeType, Double order) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeType = placeType;
        this.order = order;
        this.images = new ArrayList<>();
        this.isVisited = false;
    }

    public void updateOrder(double order) {
        this.order = order;
    }

    public void addImages(List<Image> images) {
        this.images.addAll(images);
    }

    public void clearImages() {
        this.images.clear();
    }
}
