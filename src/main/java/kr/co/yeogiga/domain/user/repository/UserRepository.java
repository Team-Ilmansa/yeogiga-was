package kr.co.yeogiga.domain.user.repository;

import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u " +
            "FROM users u " +
            "JOIN oauth o ON u.id = o.user.id " +
            "WHERE o.platform = :platform AND o.platformId = :platformId")
    Optional<User> findByPlatformAndPlatformId(@Param("platform") OAuthPlatform platform, @Param("platformId") String platformId);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}