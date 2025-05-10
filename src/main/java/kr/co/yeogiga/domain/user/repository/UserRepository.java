package kr.co.yeogiga.domain.user.repository;

import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u.id, u.username, u.password, u.email, u.nickname, u.signed_up, u.role, u.deleted_at, u.created_at, u.modified_at " +
                   "FROM users u " +
                   "INNER JOIN oauth o ON u.id = o.user_id " +
                   "WHERE o.platform = :platform AND o.platform_id = :platformId", nativeQuery = true)
    Optional<User> findUserIncludeDeletedByPlatformAndPlatformId(@Param(value = "platform") OAuthPlatform platform, @Param("platformId") String platformId);

    @Query(value = "SELECT * " +
                   "FROM users " +
                   "WHERE id = :id", nativeQuery = true)
    Optional<User> findUserIncludeDeletedById(@Param(value = "id") Long id);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * " +
            "FROM users " +
            "WHERE username = :username", nativeQuery = true)
    Optional<User> findUserIncludeDeletedByUsername(@Param(value = "username") String username);

    @Query(value = "SELECT id " +
                   "FROM users " +
                   "WHERE deleted_at IS NOT NULL AND deleted_at <= :date", nativeQuery = true)
    List<Long> findDeletedUserIdBefore(@Param(value = "date") LocalDate date);

    boolean existsByUsername(String username);

    @Modifying
    @Query(value = "DELETE " +
            "FROM users " +
            "WHERE id IN :ids", nativeQuery = true)
    void deleteHardAllByIds(List<Long> ids);
}