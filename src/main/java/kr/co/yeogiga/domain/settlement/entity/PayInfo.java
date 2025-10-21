package kr.co.yeogiga.domain.settlement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
    
    @Builder
    public PayInfo(Long userId, Long price, boolean isCompleted, Settlement settlement) {
        this.userId = userId;
        this.price = price;
        this.isCompleted = isCompleted;
        this.settlement = settlement;
        settlement.addPayInfo(this);
    }
    
    public void update(Long price) {
        this.price = price;
    }
    
    public void complete() {
        this.isCompleted = true;
    }
    
    public void uncomplete() {
        this.isCompleted = false;
    }
}
