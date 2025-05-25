package kr.co.yeogiga.application.pin.dto;

import kr.co.yeogiga.domain.pin.entity.Pin;
import lombok.Builder;

import java.time.LocalDateTime;

public class PinReq {

    @Builder
    public record Creation(
            String place,
            double latitude,
            double longitude,
            LocalDateTime time
    ) {
        public Pin toEntity() {
            return Pin.builder()
                    .place(place)
                    .latitude(latitude)
                    .longitude(longitude)
                    .time(time)
                    .build();
        }
    }
}
