package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Modifying
    @Query("UPDATE trip " +
            "SET travelStatus = 'IN_PROGRESS' " +
            "WHERE travelStatus != 'IN_PROGRESS' AND travelStatus != 'SETTING' AND startedAt <= :time AND :time <= endedAt")
    void updateTravelStatusInProgress(@Param(value = "time") LocalDateTime time);

    @Modifying
    @Query("UPDATE trip " +
            "SET travelStatus = 'COMPLETED' " +
            "WHERE travelStatus != 'COMPLETED' AND travelStatus != 'SETTING' AND endedAt <= :time")
    void updateTravelStatusCompleted(@Param(value = "time") LocalDateTime time);

    List<Trip> findAllByIdIn(Set<Long> ids);

    @Query("SELECT t.leaderId FROM trip t WHERE t.id = :tripId")
    Optional<Long> findLeaderIdById(@Param(value = "tripId") Long tripId);
}
