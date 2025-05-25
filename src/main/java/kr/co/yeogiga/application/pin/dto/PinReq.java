package kr.co.yeogiga.application.pin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.yeogiga.domain.pin.entity.Pin;
import lombok.Builder;

import java.time.LocalDateTime;

public class PinReq {

    @Builder
    public record Creation(
            @NotBlank(message = "집결지 장소는 필수 입력값입니다.")
            String place,

            @Min(value = -90, message = "위도는 -90도보다 작을 수 없습니다.")
            @Max(value = 90, message = "위도는 90도보다 클 수 없습니다.")
            @NotNull(message = "위도는 필수 입력값입니다.")
            Double latitude,

            @Min(value = -180, message = "경도는 -180도보다 작을 수 없습니다.")
            @Max(value = 180, message = "경도는 180도보다 클 수 없습니다.")
            @NotNull(message = "경도는 필수 입력값입니다.")
            Double longitude,

            @NotNull(message = "집결 시간은 필수 입력값입니다.")
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
