package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.application.trip.dto.TripRes;

import java.util.List;
import java.util.Optional;

public interface CustomTripMemberRepository {
    Optional<TripRes.TripSummary> findTripSummaryByTripId(Long tripId);
    List<TripRes.TripSummary> findAllTripSummaryByUserId(Long userId);
}
