package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.dto.TripDto;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomTripMemberRepository {
    Optional<TripDto.Summary> findTripSummaryByTripId(Long tripId);
    Page<TripDto.Summary> findAllTripSummaryByUserId(Long userId, TravelStatus travelStatus, Pageable pageable);
}
