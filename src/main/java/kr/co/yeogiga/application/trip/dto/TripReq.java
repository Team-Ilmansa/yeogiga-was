package kr.co.yeogiga.application.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class TripReq {

    @Schema(name = "TripReq.Creation", description = "여행 생성 DTO")
    public record Creation(

            @Schema(description = "여행 제목", example = "여기가 여행")
            String title,

            @Schema(description = "목적지 도시", example = "대구광역시")
            String city
    ) {
    }
}
