package kr.co.yeogiga.application.pin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.yeogiga.domain.pin.entity.Pin;
import lombok.Builder;

import java.time.LocalDateTime;

public class PinReq {

    @Builder
    @Schema(name = "PinReq.Creation", description = "집결지 핀 생성 요청 DTO")
    public record Creation(
            @Schema(description = "장소", example = "경상북도 경산시 대학로 280")
            @NotBlank(message = "집결지 장소는 필수 입력값입니다.")
            String place,

            @Schema(description = "위도", example = "36.0567")
            @Min(value = -90, message = "위도는 -90도보다 작을 수 없습니다.")
            @Max(value = 90, message = "위도는 90도보다 클 수 없습니다.")
            @NotNull(message = "위도는 필수 입력값입니다.")
            Double latitude,

            @Schema(description = "경도", example = "128.5111")
            @Min(value = -180, message = "경도는 -180도보다 작을 수 없습니다.")
            @Max(value = 180, message = "경도는 180도보다 클 수 없습니다.")
            @NotNull(message = "경도는 필수 입력값입니다.")
            Double longitude,

            @Schema(description = "집결 시간", example = "2025-05-26T12:01:00")
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
