package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
}
