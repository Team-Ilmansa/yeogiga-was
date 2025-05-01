package kr.co.yeogiga.domain.tripplace.repository;

import kr.co.yeogiga.domain.tripplace.entity.Place;

public interface CustomTripDayPlaceRepository {
    void savePlace(String id, Place place);
    void deletePlace(String id, String placeId);
    Double findOrderByIdAndPlaceId(String id, String placeId);
}
