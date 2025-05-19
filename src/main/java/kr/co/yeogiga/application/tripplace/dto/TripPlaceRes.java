package kr.co.yeogiga.application.tripplace.dto;

import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;

import java.util.List;

public class TripPlaceRes {

    public record TripDayPlaceInfo(
            String id,
            int day,
            List<PlaceSummary> places
    ) {
        public static TripDayPlaceInfo from(TripDayPlace tripDayPlace) {
            List<PlaceSummary> summaries = tripDayPlace.getPlaces().stream()
                    .map(PlaceSummary::from)
                    .toList();

            return new TripDayPlaceInfo(tripDayPlace.getId(), tripDayPlace.getDay(), summaries);
        }
    }

    public record PlaceSummary(
            String id,
            String name,
            String placeType,
            boolean isVisited
    ) {
        public static PlaceSummary from(Place place) {
            return new PlaceSummary(place.getId(), place.getName(), place.getPlaceType(), place.isVisited());
        }
    }

    public record PlaceDetails(
            String id,
            String name,
            double latitude,
            double longitude,
            String placeType,
            boolean isVisited
    ) {
        public static PlaceDetails from(Place place) {
            return new PlaceDetails(place.getId(), place.getName(), place.getLatitude(),
                    place.getLongitude(), place.getPlaceType(), place.isVisited());
        }
    }
}
