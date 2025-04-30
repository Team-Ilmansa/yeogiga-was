package kr.co.yeogiga.application.trip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;

import java.util.List;
import java.util.UUID;

public class TripPlaceDto {

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

    public record InsertRequest(
            String name,
            double latitude,
            double longitude,
            String placeType,
            String prevPlaceId,
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

    public record ReorderRequest(
            List<String> orderedPlaceIds
    ) { }
}
