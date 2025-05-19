package kr.co.yeogiga.application.tripplace.image.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TripPlaceImageRes {

    @Builder
    public record PlaceImageInfo(
            String id,
            String name,
            double latitude,
            double longitude,
            String placeType,
            List<ImageDto> images
    ) {
        public static PlaceImageInfo from(Place place) {
            return new PlaceImageInfo(
                    place.getId(),
                    place.getName(),
                    place.getLatitude(),
                    place.getLongitude(),
                    place.getPlaceType(),
                    place.getImages().stream()
                            .map(ImageDto::from)
                            .collect(Collectors.toList())
            );
        }
    }

    @Builder
    public record UnmatchedImageInfo(
            List<ImageDto> images
    ) {
        public static UnmatchedImageInfo from(List<Image> unmatchedImages) {
            return new UnmatchedImageInfo(
                    unmatchedImages.stream()
                            .map(ImageDto::from)
                            .collect(Collectors.toList())
            );
        }
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageDto(
            String id,
            String url,
            Double latitude,
            Double longitude,
            LocalDateTime date,
            boolean favorite
    ) {
        public static ImageDto from(Image image) {
            return new ImageDto(
                    image.getId(),
                    image.getUrl(),
                    image.getLatitude(),
                    image.getLongitude(),
                    image.getDate(),
                    image.isFavorite()
            );
        }
    }
}
