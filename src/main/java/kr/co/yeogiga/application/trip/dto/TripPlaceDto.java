package kr.co.yeogiga.application.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TripPlaceDto {

    @Builder
    @Schema(name = "TripPlaceDto.Request", description = "여행 목적지 추가 DTO")
    public record Request(
            @Schema(description = "목적지 이름", example = "광화문")
            String name,
            @Schema(description = "목적지 위도", example = "33.33")
            double latitude,
            @Schema(description = "목적지 경도", example = "123.123")
            double longitude,
            @Schema(description = "목적지 타입(카테고리)", example = "관광명소")
            String placeType
    ) {
        public StoredFormat toStoredFormat() {
            return new StoredFormat(
                    UUID.randomUUID().toString(),
                    name,
                    latitude,
                    longitude,
                    PlaceCategory.fromLabel(placeType).getGroupName()
            );
        }
    }

    public record StoredFormat(
            String id,
            String name,
            double latitude,
            double longitude,
            String placeCategory
    ) { }

    @Schema(name = "TripPlaceDto.CompleteRequest", description = "여행 목적지 선택 완료 요청 DTO")
    public record CompleteRequest(
            @Schema(description = "편집 완료된 마지막 일차", example = "5")
            int lastDay
    ) { }

    @Builder
    @Schema(name = "TripPlaceDto.InsertRequest", description = "삽입 위치를 포함한 새로운 목적지 추가 요청 DTO")
    public record InsertRequest(
            @Schema(description = "목적지 이름", example = "광화문")
            String name,
            @Schema(description = "위도", example = "37.57")
            double latitude,
            @Schema(description = "경도", example = "126.98")
            double longitude,
            @Schema(description = "장소 타입", example = "관광명소")
            String placeType,
            @Schema(description = "이전 목적지 ID (nullable)", example = "prevId")
            String prevPlaceId,
            @Schema(description = "다음 목적지 ID (nullable)", example = "nextId")
            String nextPlaceId
    ) {
        public Place toEntity(Double order) {
            return Place.builder()
                    .id(UUID.randomUUID().toString())
                    .name(name)
                    .latitude(latitude)
                    .longitude(longitude)
                    .order(order)
                    .placeType(PlaceCategory.fromLabel(placeType).getGroupName())
                    .build();
        }
    }

    @Schema(name = "TripPlaceDto.ReorderRequest", description = "여행 목적지 순서 변경 요청 DTO")
    public record ReorderRequest(
            @Schema(description = "정렬된 목적지 ID 리스트", example = "[\"place3-id\", \"place1-id\", \"place2-id\"]")
            List<String> orderedPlaceIds
    ) { }
}
