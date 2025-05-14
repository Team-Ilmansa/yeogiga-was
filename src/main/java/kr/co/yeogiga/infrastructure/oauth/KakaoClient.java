package kr.co.yeogiga.infrastructure.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.dto.UserInfoDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.oauth.exception.OAuthErrorType;
import kr.co.yeogiga.infrastructure.properties.OAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KakaoClient extends OAuthClient {

    public KakaoClient(ObjectMapper objectMapper, OAuthProperties oAuthProperties) {
        super(objectMapper, oAuthProperties.getKakao());
    }

    public String fetchAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());
        body.add("redirect_uri", properties.getRedirectUri());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    properties.getTokenUri(),
                    HttpMethod.POST,
                    request,
                    String.class
            ) ;

            JsonNode root = objectMapper.readTree(response.getBody());

            return root.get("access_token").asText();
        } catch (Exception e) {
            log.error("[OAUTH ERROR:kakao] {}", e.getMessage());
            throw new CustomException(OAuthErrorType.OAUTH_ERROR);
        }
    }

    public UserInfoDto fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    properties.getUserInfoUri(),
                    HttpMethod.GET,
                    request,
                    String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String platformId = root.get("id").asText();

            return UserInfoDto.of(platformId);
        } catch (Exception e) {
            log.error("[OAUTH ERROR:kakao] {}", e.getMessage());
            throw new CustomException(OAuthErrorType.OAUTH_ERROR);
        }
    }
}