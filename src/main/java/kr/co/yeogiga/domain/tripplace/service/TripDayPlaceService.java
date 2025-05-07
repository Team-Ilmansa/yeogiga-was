package kr.co.yeogiga.domain.tripplace.service;

import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.repository.TripDayPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripDayPlaceService {
    private final TripDayPlaceRepository tripDayPlaceRepository;

    public void save(TripDayPlace tripDayPlace) {
        tripDayPlaceRepository.save(tripDayPlace);
    }

    public void saveAll(List<TripDayPlace> tripDayPlaces) {
        tripDayPlaceRepository.saveAll(tripDayPlaces);
    }

    public void savePlace(String id, Place place) {
        tripDayPlaceRepository.savePlace(id, place);
    }

    public Optional<TripDayPlace> readById(String id) {
        return tripDayPlaceRepository.findById(id);
    }

    public Optional<TripDayPlace> readByIdSortedByOrder(String id) {
        return tripDayPlaceRepository.findByIdSorted(id);
    }

    public List<TripDayPlace> readByTripIdSortedByOrder(Long tripId) {
        return tripDayPlaceRepository.findByTripIdSorted(tripId);
    }

    public Double readOrderByIdAndPlaceId(String id, String placeId) {
        return tripDayPlaceRepository.findOrderByIdAndPlaceId(id, placeId);
    }

    public void deletePlace(String id, String placeId) {
        tripDayPlaceRepository.deletePlace(id, placeId);
    }
}
