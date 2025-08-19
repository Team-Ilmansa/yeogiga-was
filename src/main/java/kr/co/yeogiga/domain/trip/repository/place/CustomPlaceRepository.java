package kr.co.yeogiga.domain.trip.repository.place;

import kr.co.yeogiga.domain.trip.entity.Place;

import java.util.List;

public interface CustomPlaceRepository {
    void saveAllInBatch(List<Place> places);
}
