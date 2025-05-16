package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    Optional<TripMember> findByTripIdAndUserId(Long tripId, Long userId);
}
