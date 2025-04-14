package kr.co.yeogiga.domain.oauth.entity;

import jakarta.persistence.*;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "oauth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthPlatform platform;

    @Column(name = "platform_id", nullable = false)
    private String platformId;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public OAuth(OAuthPlatform platform, String platformId, User user) {
        this.platform = platform;
        this.platformId = platformId;
        this.user = user;
    }
}