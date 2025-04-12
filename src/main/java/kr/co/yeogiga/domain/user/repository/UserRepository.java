package kr.co.yeogiga.domain.user.repository;

import kr.co.yeogiga.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}