package kr.co.yeogiga.application.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
            String title,

            @Schema(description = "목적지 도시", example = "대구광역시")
            @Size(max = 20, message = "여행 도시는 최대 20글자까지 가능합니다.")
            String city
    ) {
    }

    @Builder
    @Schema(name = "TripReq.Time", description = "여행 시간 수정 요청 DTO")
    public record Time(
            @Schema(description = "여행 시작 시각", example = "2025-01-01T12:00:00", type = "string")
            @NotNull(message = "여행 시작 시각은 필수 입력값입니다.")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            @JsonSerialize(using = LocalDateTimeSerializer.class)
            LocalDateTime start,

            @Schema(description = "여행 종료 시각", example = "2025-01-05T12:00:00", type = "string")
            @NotNull(message = "여행 종료 시각은 필수 입력값입니다.")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            @JsonSerialize(using = LocalDateTimeSerializer.class)
            LocalDateTime end
    ) {
        @Schema(hidden = true)
        public boolean isValid() {
            return start.isBefore(end);
        }
    }
}
