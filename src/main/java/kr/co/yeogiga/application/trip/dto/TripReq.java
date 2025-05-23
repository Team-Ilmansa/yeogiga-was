package kr.co.yeogiga.application.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

public class TripReq {

    @Builder
    @Schema(name = "TripReq.Creation", description = "여행 생성 DTO")
    public record Creation(

            @Schema(description = "여행 제목", example = "여기가 여행")
            @NotBlank(message = "제목은 필수 입력값입니다.")
            @Size(max = 20, message = "제목은 최대 20글자까지 가능합니다.")
            String title
    ) {
    }

    @Builder
    @Schema(name = "TripReq.Time", description = "여행 시간 수정 요청 DTO")
    public record Time(
            @Schema(description = "여행 시작 시각", example = "2025-01-01T12:00:00", type = "string")
            @NotNull(message = "여행 시작 시각은 필수 입력값입니다.")
            LocalDateTime start,

            @Schema(description = "여행 종료 시각", example = "2025-01-05T12:00:00", type = "string")
            @NotNull(message = "여행 종료 시각은 필수 입력값입니다.")
            LocalDateTime end
    ) {
        @Schema(hidden = true)
        public boolean isValid() {
            if (start == null || end == null) return false;
            return start.isBefore(end);
        }
    }

    @Builder
    @Schema(name = "TripReq.Update", description = "여행 정보 수정 요청 DTO")
    public record Update(
            @Schema(description = "여행 제목", example = "new title")
            @NotBlank(message = "제목은 필수 입력값입니다.")
            @Size(max = 20, message = "제목은 최대 20글자까지 가능합니다.")
            String title
    ) {
    }
}
