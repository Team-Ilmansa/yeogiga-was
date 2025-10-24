package kr.co.yeogiga.application.tripplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TripPlaceReqLegacy {

    @Builder
    @Schema(name = "TripPlaceReqLegacy.Request", description = "여행 목적지 추가 DTO")
    public record Request(
            @Schema(description = "목적지 이름", example = "광화문")
            String name,
            @Schema(description = "주소", example = "서울특별시 종로구 세종대로 172")
            String address,
            @Schema(description = "목적지 위도", example = "33.33")
            double latitude,
            @Schema(description = "목적지 경도", example = "123.123")
            double longitude,
            @Schema(description = "목적지 타입(카테고리)", example = "RESTAURANT, TOURISM, LODGING, TRANSPORT, ETC")
            PlaceCategory placeType
    ) {
        public Place toEntity(Double order) {
            return Place.builder()
                    .id(UUID.randomUUID().toString())
                    .name(name)
                    .latitude(latitude)
                    .longitude(longitude)
                    .order(order)
                    .placeType(placeType)
                    .build();
        }

        public StoredFormat toStoredFormat() {
            return new StoredFormat(
                    UUID.randomUUID().toString(),
                    name,
                    address,
                    latitude,
                    longitude,
                    placeType
            );
        }
    }

    public record StoredFormat(
            String id,
            String name,
            String address,
            double latitude,
            double longitude,
            PlaceCategory placeCategory
    ) { }

    @Schema(name = "TripPlaceReqLegacy.CompleteRequest", description = "여행 목적지 선택 완료 요청 DTO")
    public record CompleteRequest(
            @Schema(description = "편집 완료된 마지막 일차", example = "5")
            int lastDay
    ) { }

    @Schema(name = "TripPlaceReqLegacy.ReorderRequest", description = "여행 목적지 순서 변경 요청 DTO")
    public record ReorderRequest(
            @Schema(description = "정렬된 목적지 ID 리스트", example = "[\"place3-id\", \"place1-id\", \"place2-id\"]")
            List<String> orderedPlaceIds
    ) { }
}
