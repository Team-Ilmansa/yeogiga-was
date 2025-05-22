package kr.co.yeogiga.domain.trip.service;

import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;

    public void save(Trip trip) {
        tripRepository.save(trip);
    }

    public Optional<Trip> readById(Long tripId) {
        return tripRepository.findById(tripId);
    }

    public List<Trip> readAllByIds(Set<Long> ids) {
        return tripRepository.findAllByIdIn(ids);
    }

    public void updateAllTravelStatusToInProgress(LocalDateTime time) {
        tripRepository.updateTravelStatusInProgress(time);
    }

    public void updateAllTravelStatusToCompleted(LocalDateTime time) {
        tripRepository.updateTravelStatusCompleted(time);
    }

    public void deleteById(Long tripId) {
        tripRepository.deleteById(tripId);
    }
}
