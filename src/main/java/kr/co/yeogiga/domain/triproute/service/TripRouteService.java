package kr.co.yeogiga.domain.triproute.service;

import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import kr.co.yeogiga.domain.triproute.repository.TripRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripRouteService {
    private final TripRouteRepository tripRouteRepository;

    public void saveAll(List<TripRoute> tripRoutes) {
        tripRouteRepository.saveAllInBatch(tripRoutes);
    }

    public Optional<TripRoute> readByTripIdAndDay(Long tripId, int day) {
        return tripRouteRepository.findByTripIdAndDay(tripId, day);
    }

    public List<TripRoute> readByTripId(Long tripId) {
        return tripRouteRepository.findByTripIdOrderByDay(tripId);
    }
}
