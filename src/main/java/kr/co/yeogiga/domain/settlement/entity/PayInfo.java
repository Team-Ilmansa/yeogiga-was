package kr.co.yeogiga.domain.settlement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "pay_info")
public class PayInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long price;
    
    @Column(name = "is_completed")
    private boolean isCompleted;
    
    @Column(name = "settlement_id", nullable = false)
    private Long settlementId;
    
    @Builder
    public PayInfo(Long userId, Long price, boolean isCompleted, Long settlementId) {
        this.userId = userId;
        this.price = price;
        this.isCompleted = isCompleted;
        this.settlementId = settlementId;
    }
}
