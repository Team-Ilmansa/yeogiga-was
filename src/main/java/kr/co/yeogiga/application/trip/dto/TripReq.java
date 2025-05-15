package kr.co.yeogiga.application.trip.dto;

public class TripReq {

    public record Creation(
            String title,
            String city
    ) {
    }
}
