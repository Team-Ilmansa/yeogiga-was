package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;

import java.util.List;
import java.util.Optional;

public interface CustomTripDayPlaceRepository {
    void savePlace(String id, Place place);
    void deletePlace(String id, String placeId);
    Double findOrderByIdAndPlaceId(String id, String placeId);
    Optional<TripDayPlace> findByIdSorted(String id);
    List<TripDayPlace> findByTripIdSorted(Long tripId);
}
