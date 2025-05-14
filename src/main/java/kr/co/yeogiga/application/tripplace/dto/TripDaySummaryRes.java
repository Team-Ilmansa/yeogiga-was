package kr.co.yeogiga.application.tripplace.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;

import java.time.LocalDateTime;
import java.util.List;

public class TripDaySummaryRes {

    public record DayDto(
            String id,
            Integer day,
            List<PlaceDto> places,
            ImageDto unmatchedImage
    ) {
        public static DayDto from(TripDayPlace tripDayPlace) {
            return new DayDto(
                    tripDayPlace.getId(),
                    tripDayPlace.getDay(),
                    tripDayPlace.getPlaces().stream()
                            .filter(p -> p.getId() != null)
                            .map(PlaceDto::from)
                            .toList(),
                    tripDayPlace.getUnmatchedImages().stream()
                            .findFirst()
                            .map(ImageDto::from)
                            .orElse(null)
            );
        }
    }

    public record PlaceDto(
            String id,
            String name,
            Double latitude,
            Double longitude,
            String type,
            ImageDto image
    ) {
        public static PlaceDto from(Place place) {
            return new PlaceDto(
                    place.getId(),
                    place.getName(),
                    place.getLatitude(),
                    place.getLongitude(),
                    place.getPlaceType(),
                    place.getImages().stream()
                            .findFirst()
                            .map(ImageDto::from)
                            .orElse(null)
            );
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageDto(
            String id,
            String url,
            Double latitude,
            Double longitude,
            LocalDateTime date
    ) {
        public static ImageDto from(Image image) {
            return new ImageDto(
                    image.getId(),
                    image.getUrl(),
                    image.getLatitude(),
                    image.getLongitude(),
                    image.getDate()
            );
        }
    }
}
