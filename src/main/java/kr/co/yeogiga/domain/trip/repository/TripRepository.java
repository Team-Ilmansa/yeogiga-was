package kr.co.yeogiga.domain.trip.repository;

import kr.co.yeogiga.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Modifying
    @Query("UPDATE trip " +
            "SET travelStatus = 'IN_PROGRESS' " +
            "WHERE travelStatus != 'IN_PROGRESS' AND startedAt <= :time AND :time <= endedAt")
    void updateTravelStatusInProgress(@Param(value = "time") LocalDateTime time);

    @Modifying
    @Query("UPDATE trip " +
            "SET travelStatus = 'COMPLETED' " +
            "WHERE travelStatus != 'COMPLETED' AND endedAt <= :time")
    void updateTravelStatusCompleted(@Param(value = "time") LocalDateTime time);

}
