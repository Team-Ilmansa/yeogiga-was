package kr.co.yeogiga.domain.tripplace.service;

import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.repository.TripDayPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripDayPlaceService {
    private final TripDayPlaceRepository tripDayPlaceRepository;

    public void saveAll(List<TripDayPlace> tripDayPlaces) {
        tripDayPlaceRepository.saveAll(tripDayPlaces);
    }
}
