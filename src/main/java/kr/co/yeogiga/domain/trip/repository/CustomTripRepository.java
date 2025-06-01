package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.dto.TripFcmTokenInfoDto;
import kr.co.yeogiga.domain.trip.dto.TripFcmTokenQueryDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomTripRepository {
    List<TripFcmTokenQueryDto> findTripFcmTokensByTime(LocalDateTime time);

    List<TripFcmTokenInfoDto> findTripFcmTokenInfosById(Long tripId);
}
