package kr.co.yeogiga.domain.triproute.repository;

import kr.co.yeogiga.domain.triproute.entity.TripRoute;

import java.util.List;

public interface CustomTripRouteRepository {
    void saveAllInBatch(List<TripRoute> tripRoutes);
}
