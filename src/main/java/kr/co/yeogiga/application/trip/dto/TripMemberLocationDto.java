package kr.co.yeogiga.application.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TripMemberLocationDto {

    @Schema(name = "TripMemberLocationDto.Request", description = "여행 멤버 위치 저장 요청 DTO")
    public record Request(
            @Schema(description = "위도", example = "33.3")
            @Min(value = -90, message = "위도는 -90도보다 작을 수 없습니다.")
            @Max(value = 90, message = "위도는 90도보다 클 수 없습니다.")
            @NotNull(message = "위도는 필수 입력값입니다.")
            Double latitude,
            
            @Schema(description = "경도", example = "120.3")
            @Min(value = -180, message = "경도는 -180도보다 작을 수 없습니다.")
            @Max(value = 180, message = "경도는 180도보다 클 수 없습니다.")
            @NotNull(message = "경도는 필수 입력값입니다.")
            Double longitude
    ) {
        public StoredFormat toStoredFormat(Long userId) {
            return new StoredFormat(latitude, longitude, userId);
        }
    }

    public record StoredFormat(
            Double latitude,
            Double longitude,
            Long userId
    ) { }
}
