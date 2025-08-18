package kr.co.yeogiga.application.tripplace.image.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.yeogiga.domain.placeimage.entity.Image;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FavoriteImageRes(
        String id,
        String url,
        Double latitude,
        Double longitude,
        LocalDateTime date,
        boolean favorite
) {
    public static FavoriteImageRes from(Image image) {
        return new FavoriteImageRes(
                image.getId(),
                image.getUrl(),
                image.getLatitude(),
                image.getLongitude(),
                image.getDate(),
                image.isFavorite()
        );
    }
}
