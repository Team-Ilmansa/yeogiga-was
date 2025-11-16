package kr.co.yeogiga.application.uprisingplace.dto;

import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import kr.co.yeogiga.domain.uprisingplace.entity.UprisingPlace;
import lombok.Builder;

public class UprisingPlaceDto {
    
    @Builder
    public record Response(
            Long id,
            String name,
            String address,
            PlaceCategory placeCategory,
            String url
    ) {
        public static Response from(UprisingPlace uprisingPlace) {
            return Response.builder()
                    .id(uprisingPlace.getId())
                    .name(uprisingPlace.getName())
                    .address(uprisingPlace.getAddress())
                    .placeCategory(uprisingPlace.getPlaceCategory())
                    .url(uprisingPlace.getUrl())
                    .build();
        }
    }
}
