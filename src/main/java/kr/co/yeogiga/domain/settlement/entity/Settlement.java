package kr.co.yeogiga.domain.settlement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.yeogiga.domain.settlement.type.SettlementType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "settlement")
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trip_id", nullable = false)
    private Long tripId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "total_price", nullable = false)
    private Long totalPrice;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Enumerated(value = EnumType.STRING)
    private SettlementType type;
    
    @Column(name = "payer_id", nullable = false)
    private Long payerId;
    
    @Column(name = "is_completed")
    private boolean isCompleted;
    
    @Builder
    public Settlement(
            Long tripId,
            String name,
            Long totalPrice,
            LocalDate date,
            SettlementType type,
            Long payerId,
            boolean isCompleted
    ) {
        this.tripId = tripId;
        this.name = name;
        this.totalPrice = totalPrice;
        this.date = date;
        this.type = type;
        this.payerId = payerId;
        this.isCompleted = isCompleted;
    }
}
