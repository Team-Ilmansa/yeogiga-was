package kr.co.yeogiga.domain.tripplace.entity;

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
    private String placeType;
    private Double order;
    private List<Image> images;

    @Builder
    public Place(String id, String name, Double latitude,
                 Double longitude, String placeType, Double order) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeType = placeType;
        this.order = order;
        this.images = new ArrayList<>();
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
