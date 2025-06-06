package kr.co.yeogiga.domain.user.repository;

import kr.co.yeogiga.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    Optional<User> findByFcmToken(String fcmToken);
    
    List<User> findAllByIdIn(List<Long> ids);
}