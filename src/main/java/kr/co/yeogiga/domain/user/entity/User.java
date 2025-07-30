package kr.co.yeogiga.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.yeogiga.domain.common.entity.BaseTimeEntity;
import kr.co.yeogiga.domain.user.type.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "users")
@DynamicUpdate
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "fcm_token")
    private String fcmToken;

    @ColumnDefault("false")
    @Column(name = "signed_up", nullable = false)
    private boolean signedUp;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "deleted_At")
    private LocalDateTime deletedAt;

    @Builder
    public User(Long id, String username, String password, String nickname, String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    public void upgradeRoleToUser() {
        signedUp = true;
        role = Role.USER;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void revertWithdrawal() {
        this.deletedAt = null;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void clearFcmToken() {
        this.fcmToken = null;
    }

    public void updateProfileImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public boolean isSameNickname(String nickname) {
        return this.nickname.equals(nickname);
    }
}