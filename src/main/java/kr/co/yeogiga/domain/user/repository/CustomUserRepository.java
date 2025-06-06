package kr.co.yeogiga.domain.user.repository;

import kr.co.yeogiga.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomUserRepository {
    Optional<User> findUserIncludeDeletedByPlatformAndPlatformId(String platform, String platformId);
    Optional<User> findUserIncludeDeletedById(Long id);
    Optional<User> findUserIncludeDeletedByNickname(String nickname);
    Optional<User> findUserIncludeDeletedByUsername(String username);
    List<Long> findDeletedUserIdBefore(LocalDate date);
    boolean existsIncludeDeletedByUsername(String username);
    boolean existsIncludeDeletedByNickname(String nickname);
    boolean existsIdIncludeDeletedByEmail(String email);
    void deleteHardAllByIdIn(List<Long> ids);
}
