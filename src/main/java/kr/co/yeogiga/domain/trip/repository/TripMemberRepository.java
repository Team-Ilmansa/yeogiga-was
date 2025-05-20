package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    boolean existsByTripIdAndUserId(Long tripId, Long userId);

    @Query("SELECT tm.trip FROM trip_member tm WHERE tm.user.id = :userId ORDER BY tm.trip.startedAt ASC NULLS LAST")
    List<Trip> findAllTripByUserId(@Param(value = "userId") Long userId);
}
