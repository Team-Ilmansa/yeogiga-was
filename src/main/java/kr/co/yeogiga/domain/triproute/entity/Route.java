package kr.co.yeogiga.domain.triproute.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Route {
    private double latitude;
    private double longitude;
    private LocalDateTime time;

    @Builder
    public Route(double latitude, double longitude, LocalDateTime time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}
