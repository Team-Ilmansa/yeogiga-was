package kr.co.yeogiga.application.tripplace.image.dto;

import java.util.List;

public class TripPlaceImageDeleteDto {

    public record SingleDeleteReq(
            DeleteType deleteType,
            String placeId
    ) { }

    public record MultiDeleteReq(
            List<String> imageIds
    ) { }

    public enum DeleteType {
        PLACE, UNMATCHED
    }
}
