package kr.co.yeogiga.application.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

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
}
