package kr.co.yeogiga.application.tripplace.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ImageFavoriteReq", description = "이미지 즐겨찾기 DTO")
public record ImageFavoriteReq(
        @Schema(description = "목적지 ID (unmatched 이미지이면 placeId를 보내지 말아주세요 - null로 인식 예정)", example = "place-id")
        String placeId,
        @Schema(description = "이미지 즐겨찾기 상태 변경 여부", example = "true")
        boolean favorite
) {
}
