package kr.co.yeogiga.domain.tripplace.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Image {
    private String id;
    private String url;
    private Double latitude;
    private Double longitude;
    private LocalDateTime date;

    @Builder
    public Image(String url, Double latitude, Double longitude, LocalDateTime date) {
        this.id = UUID.randomUUID().toString();
        this.url = url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public void updateGps(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
