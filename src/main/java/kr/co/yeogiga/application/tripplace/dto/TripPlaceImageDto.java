package kr.co.yeogiga.application.tripplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class TripPlaceImageDto {

    @Schema(name = "TripPlaceImageDto.ImageMoveReq", description = "같은 날짜 내 이미지 이동 요청 DTO")
    public record ImageMoveReq(
            @Schema(description = "이미지가 현재 속한 목적지 ID", example = "place1-id")
            String fromPlaceId,
            @Schema(description = "이미지를 옮길 목적지 ID", example = "place2-id")
            String toPlaceId,
            @Schema(description = "이동할 이미지 ID", example = "image-id")
            String imageId
    ) { }

    @Schema(name = "TripPlaceImageDto.ImageCrossDayMoveReq", description = "다른 날짜 간 이미지 이동 요청 DTO")
    public record ImageCrossDayMoveReq(
            @Schema(description = "이미지가 속한 원본 TripDayPlace ID", example = "trip-day-1-id")
            String fromTripDayPlaceId,
            @Schema(description = "이미지가 현재 속한 목적지 ID", example = "place1-id")
            String fromPlaceId,
            @Schema(description = "이미지를 이동할 TripDayPlace ID", example = "trip-day-2-id")
            String toTripDayPlaceId,
            @Schema(description = "이미지를 옮길 목적지 ID", example = "place2-id")
            String toPlaceId,
            @Schema(description = "이동할 이미지 ID", example = "image-id")
            String imageId
    ) { }

    @Schema(name = "TripPlaceImageDto.ImageUnmatchedMoveReq", description = "Unmatched <-> 목적지 이미지 이동 요청 DTO")
    public record ImageUnmatchedMoveReq(
            @Schema(description = "대상 Place ID (Unmatched <-> Place 이동 시 Place ID)", example = "place1-id")
            String placeId,
            @Schema(description = "이동할 이미지 ID", example = "image-id")
            String imageId
    ) { }
}
