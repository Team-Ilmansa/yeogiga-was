package kr.co.yeogiga.domain.oauth.repository;

import kr.co.yeogiga.domain.oauth.entity.OAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {
}