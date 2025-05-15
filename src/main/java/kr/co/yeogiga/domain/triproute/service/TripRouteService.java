package kr.co.yeogiga.domain.triproute.service;

import kr.co.yeogiga.domain.triproute.entity.TripRoute;
import kr.co.yeogiga.domain.triproute.repository.TripRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripRouteService {
    private final TripRouteRepository tripRouteRepository;

    public void saveAll(List<TripRoute> tripRoutes) {
        tripRouteRepository.saveAllInBatch(tripRoutes);
    }
}
