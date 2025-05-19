package kr.co.yeogiga.application.tripplace.image.dto;

public record ImageFavoriteReq(
        String placeId,
        boolean favorite
) {
}
