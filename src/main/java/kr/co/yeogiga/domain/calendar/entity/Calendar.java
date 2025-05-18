package kr.co.yeogiga.domain.calendar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.yeogiga.domain.calendar.converter.LocalDateListConverter;
import kr.co.yeogiga.domain.trip.entity.Trip;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "calendar")
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "available_dates", columnDefinition = "TEXT")
    @Convert(converter = LocalDateListConverter.class)
    private List<LocalDate> availableDates;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Builder
    public Calendar(Long userId, List<LocalDate> availableDates, Trip trip) {
        this.userId = userId;
        this.availableDates = availableDates;
        this.trip = trip;
    }

    public void updateAvailableDates(List<LocalDate> availableDates) {
        this.availableDates = availableDates;
    }
}
