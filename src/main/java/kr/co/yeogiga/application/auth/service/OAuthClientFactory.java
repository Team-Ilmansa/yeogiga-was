package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.oauth.exception.OAuthErrorType;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.infrastructure.oauth.KakaoClient;
import kr.co.yeogiga.infrastructure.oauth.NaverClient;
import kr.co.yeogiga.infrastructure.oauth.OAuthClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class OAuthClientFactory {
    private final NaverClient naverClient;
    private final KakaoClient kakaoClient;

    public OAuthClient getOAuthClient(OAuthPlatform platform) {
        return switch (platform) {
            case NAVER -> naverClient;
            case KAKAO -> kakaoClient;
            default -> throw new CustomException(OAuthErrorType.UNSUPPORTED_PLATFORM);
        };
    }
}