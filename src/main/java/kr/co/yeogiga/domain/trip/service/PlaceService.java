package kr.co.yeogiga.domain.trip.service;

import kr.co.yeogiga.domain.trip.entity.Place;
import kr.co.yeogiga.domain.trip.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    public void save(Place place) {
        placeRepository.save(place);
    }

    public void saveAll(List<Place> places) {
        placeRepository.saveAllInBatch(places);
    }

    public int countByTripDayId(Long tripDayId) {
        return placeRepository.countByTripDay_Id(tripDayId);
    }
}
