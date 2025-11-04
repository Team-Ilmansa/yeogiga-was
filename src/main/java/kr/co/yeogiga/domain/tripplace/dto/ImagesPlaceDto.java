package kr.co.yeogiga.domain.tripplace.dto;

import kr.co.yeogiga.domain.placeimage.entity.Image;

import java.util.List;

public class ImagesPlaceDto {

    public record Response(
            List<PlaceImages> byPlace,
            List<Image> unmatched
    ) { }

    public record PlaceImages(
            String placeId,
            List<Image> images
    ) { }
}
