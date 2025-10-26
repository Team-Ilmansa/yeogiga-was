package kr.co.yeogiga.domain.trip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.yeogiga.domain.common.entity.BaseTimeEntity;
import kr.co.yeogiga.domain.trip.converter.CityConverter;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "trip")
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE trip SET deleted_at = NOW() WHERE id = ?")
public class Trip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(name = "leader_id", nullable = false)
    private Long leaderId;

    @Convert(converter = CityConverter.class)
    private List<String> city;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "travel_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TravelStatus travelStatus;

    @Builder
    public Trip(String title, Long leaderId, TravelStatus travelStatus) {
        this.title = title;
        this.leaderId = leaderId;
        this.travelStatus = travelStatus;
    }

    public void updateTime(LocalDateTime startedAt, LocalDateTime endedAt) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public void updateStatus(TravelStatus status) {
        this.travelStatus = status;
    }

    public void updateInfo(String title) {
        this.title = title;
    }

    public boolean isLeader(Long userId) {
        return this.leaderId != null && this.leaderId.equals(userId);
    }
    
    public void updateCity(List<String> city) {
        this.city = city;
    }
}
