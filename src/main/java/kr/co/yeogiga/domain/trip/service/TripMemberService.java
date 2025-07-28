package kr.co.yeogiga.domain.trip.service;

import kr.co.yeogiga.domain.trip.dto.TripDto;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.repository.TripMemberRepository;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripMemberService {
    private final TripMemberRepository tripMemberRepository;

    public void save(TripMember tripMember) {
        tripMemberRepository.save(tripMember);
    }

    public List<Trip> readAllTripByUserId(Long userId) {
        return tripMemberRepository.findAllTripByUserId(userId);
    }
    
    public Optional<TripDto.Summary> readTripSummaryByTripId(Long tripId) {
        return tripMemberRepository.findTripSummaryByTripId(tripId);
    }
    
    public List<TripDto.Summary> readAllTripSummaryByUserId(Long userId) {
        return tripMemberRepository.findAllTripSummaryByUserId(userId);
    }

    public List<Trip> readAllSettingTripByUserId(Long userId) {
        return tripMemberRepository.findAllSettingTripByUserId(userId);
    }

    public List<User> readAllUserByTripId(Long tripId) {
        return tripMemberRepository.findAllUserByTripId(tripId);
    }

    public List<Long> readAllUserIdByTripId(Long tripId) {
        return tripMemberRepository.findAllUserIdByTripId(tripId);
    }

    public boolean existsByTripIdAndUserId(Long tripId, Long userId) {
        return tripMemberRepository.existsByTripIdAndUserId(tripId, userId);
    }

    public void deleteByTripIdAndUserId(Long tripId, Long userId) {
        tripMemberRepository.deleteByTripIdAndUserId(tripId, userId);
    }
}
