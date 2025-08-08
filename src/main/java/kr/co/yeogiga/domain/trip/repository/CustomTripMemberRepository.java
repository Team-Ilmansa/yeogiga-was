package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.dto.TripDto;
import kr.co.yeogiga.domain.trip.type.TravelStatus;

import java.util.List;
import java.util.Optional;

public interface CustomTripMemberRepository {
    Optional<TripDto.Summary> findTripSummaryByTripId(Long tripId);
    List<TripDto.Summary> findAllTripSummaryByUserId(Long userId, TravelStatus travelStatus);
}
