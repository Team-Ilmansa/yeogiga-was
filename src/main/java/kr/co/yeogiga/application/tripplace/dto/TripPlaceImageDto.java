package kr.co.yeogiga.application.tripplace.dto;

public class TripPlaceImageDto {

    public record ImageMoveReq(
            String fromPlaceId,
            String toPlaceId,
            String imageId
    ) { }

    public record ImageCrossDayMoveReq(
            String fromTripDayPlaceId,
            String fromPlaceId,
            String toTripDayPlaceId,
            String toPlaceId,
            String imageId
    ) { }

    public record ImageUnmatchedMoveReq(
            String placeId,
            String imageId
    ) { }
}
