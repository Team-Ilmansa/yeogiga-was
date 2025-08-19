package kr.co.yeogiga.domain.trip.service;

import kr.co.yeogiga.domain.trip.entity.TripDay;
import kr.co.yeogiga.domain.trip.repository.tripday.TripDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripDayService {
    private final TripDayRepository tripDayRepository;

    public TripDay save(TripDay tripDay) {
        return tripDayRepository.save(tripDay);
    }

    public Optional<TripDay> readByTripIdAndDay(Long tripId, int day) {
        return tripDayRepository.findByTrip_IdAndDay(tripId, day);
    }
}
