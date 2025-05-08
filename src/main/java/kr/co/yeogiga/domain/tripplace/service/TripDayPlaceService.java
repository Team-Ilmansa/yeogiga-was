package kr.co.yeogiga.domain.tripplace.service;

import kr.co.yeogiga.domain.tripplace.entity.Image;
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

    public void saveImage(String id, String placeId, Image image) {
        tripDayPlaceRepository.saveImage(id, placeId, image);
    }

    public void saveImageToUnmatched(String id, Image image) {
        tripDayPlaceRepository.saveImageToUnmatched(id, image);
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

    public void deleteImage(String id, String placeId, String imageId) {
        tripDayPlaceRepository.deleteImage(id, placeId, imageId);
    }

    public void deleteImageFromUnMatched(String id, String imageId) {
        tripDayPlaceRepository.deleteImageFromUnMatched(id, imageId);
    }
}
