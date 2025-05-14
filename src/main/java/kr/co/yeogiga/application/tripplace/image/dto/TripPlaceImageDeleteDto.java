package kr.co.yeogiga.application.tripplace.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class TripPlaceImageDeleteDto {

    @Schema(name = "TripPlaceImageDeleteDto.SingleDeleteReq", description = "단일 이미지 삭제 요청 DTO")
    public record SingleDeleteReq(
            @Schema(description = "삭제할 이미지 url", example = "https://image1.com")
            String url,
            @Schema(description = "삭제 대상 타입 (PLACE - 목적지 내, UNMATCHED - 기타 항목)", example = "PLACE")
            DeleteType deleteType,
            @Schema(description = "Place ID (type이 PLACE일 경우 필수)", example = "place1-id")
            String placeId
    ) { }

    @Schema(name = "TripPlaceImageDeleteDto.MultiDeleteReq", description = "여행 이미지 벌크 삭제 요청 DTO")
    public record MultiDeleteReq(
            @Schema(description = "삭제할 이미지 ID 리스트", example = "[\"image1-id\", \"image2-id\"]")
            List<String> imageIds,
            @Schema(description = "삭제할 이미지 url 리스트", example = "[\"https://image1.com\", \"https://image2.com\"]")
            List<String> urls
    ) { }

    public enum DeleteType {
        PLACE, UNMATCHED
    }
}
