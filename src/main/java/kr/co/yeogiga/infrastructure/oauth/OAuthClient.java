package kr.co.yeogiga.infrastructure.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.dto.UserInfoDto;
import kr.co.yeogiga.infrastructure.properties.OAuthProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class OAuthClient {
    protected final ObjectMapper objectMapper;
    protected final OAuthProperties.Platform properties;

    public abstract String fetchAccessToken(String code);

    public abstract UserInfoDto fetchUserInfo(String accessToken);
}