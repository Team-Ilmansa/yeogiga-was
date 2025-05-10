package kr.co.yeogiga.domain.oauth.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthPlatform {
    NAVER, KAKAO;
}