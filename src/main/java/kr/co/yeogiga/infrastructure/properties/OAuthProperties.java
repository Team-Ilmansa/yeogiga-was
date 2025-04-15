package kr.co.yeogiga.infrastructure.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuthProperties {
    private Platform naver;
    private Platform kakao;

    @Getter
    @RequiredArgsConstructor
    public static class Platform {
        private final String clientId;
        private final String clientSecret;
        private final String redirectUri;
        private final String tokenUri;
        private final String userInfoUri;
    }
}