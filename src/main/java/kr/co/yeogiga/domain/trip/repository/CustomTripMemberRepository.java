package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.application.trip.dto.TripRes;

import java.util.List;

public interface CustomTripMemberRepository {
    List<TripRes.TripSummary> findAllTripSummaryByUserId(Long userId);
}
