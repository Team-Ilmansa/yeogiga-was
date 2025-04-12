package kr.co.yeogiga.domain.user.repository;

import kr.co.yeogiga.domain.oauth.entity.OAuth;
import kr.co.yeogiga.domain.oauth.repository.OAuthRepository;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.oauth.type.Platform;
import kr.co.yeogiga.infrastructure.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthRepository oAuthRepository;

    @Test
    @DisplayName("일반 User 생성")
    void 일반_User_생성() {
        // given
        User user = User.builder()
                .email("test@gmail.com")
                .username("tester")
                .nickname("test")
                .password("testpw")
                .build();

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("소셜 로그인 User 생성")
    void 소셜_로그인_User_생성() {
        // given
        User relatedUser = User.builder()
                .email("fromPlatformt@test.com")
                .nickname("tempNickname")
                .build();

        OAuth oauth = OAuth.builder()
                .platform(Platform.NAVER)
                .platformId("12345")
                .user(relatedUser)
                .build();
        // when
        OAuth savedOAuth = oAuthRepository.save(oauth);

        // then
        assertThat(savedOAuth.getId()).isEqualTo(oauth.getId());
    }
}