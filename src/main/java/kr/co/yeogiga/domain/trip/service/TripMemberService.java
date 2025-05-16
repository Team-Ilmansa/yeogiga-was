package kr.co.yeogiga.domain.trip.service;

import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.repository.TripMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripMemberService {
    private final TripMemberRepository tripMemberRepository;

    public void save(TripMember tripMember) {
        tripMemberRepository.save(tripMember);
    }

    public boolean existsByTripIdAndUserId(Long tripId, Long userId) {
        return tripMemberRepository.existsByTripIdAndUserId(tripId, userId);
    }
}
