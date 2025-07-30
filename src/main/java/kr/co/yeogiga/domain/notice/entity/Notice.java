package kr.co.yeogiga.domain.notice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.co.yeogiga.domain.common.entity.BaseTimeEntity;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "notice")
@Table(
        name = "notice",
        indexes = {
                @Index(name = "idx_trip_id", columnList = "trip_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
    
    @Column(name = "author_id", insertable = false, updatable = false)
    private Long authorId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(name = "trip_id", nullable = false)
    private Long tripId;
    
    @Builder
    public Notice(String title, User author, String description, Long tripId) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.tripId = tripId;
    }
    
    public void update(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    public boolean isAuthor(Long userId) {
        return this.author != null && this.authorId.equals(userId);
    }
}
