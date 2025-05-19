package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    boolean existsByTripIdAndUserId(Long tripId, Long userId);

    List<TripMember> findTripByUserId(Long userId);
}
