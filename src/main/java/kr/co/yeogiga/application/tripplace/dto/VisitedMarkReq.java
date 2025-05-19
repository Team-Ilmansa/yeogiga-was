package kr.co.yeogiga.application.tripplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VisitedMarkReq", description = "목적지 방문 여부 체크 DTO")
public record VisitedMarkReq(
        @Schema(description = "목적지 방문 변경 상태", example = "true")
        boolean isVisited
) {
}
