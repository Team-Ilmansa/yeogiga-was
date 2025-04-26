package kr.co.yeogiga.infrastructure.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.auth.dto.UserInfoDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.oauth.exception.OAuthErrorType;
import kr.co.yeogiga.infrastructure.properties.OAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class NaverClient extends OAuthClient {

    public NaverClient(ObjectMapper objectMapper, OAuthProperties oAuthProperties) {
        super(objectMapper, oAuthProperties.getNaver());
    }

    public String fetchAccessToken(String code) {
        String url = UriComponentsBuilder.fromUriString(properties.getTokenUri())
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", properties.getClientId())
                .queryParam("client_secret", properties.getClientSecret())
                .queryParam("code", code)
                .toUriString();

        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());

            return root.get("access_token").asText();
        } catch (Exception e) {
            log.error("[OAUTH ERROR:naver] {}", e.getMessage());
            throw new CustomException(OAuthErrorType.OAUTH_ERROR);
        }
    }

    public UserInfoDto fetchUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    properties.getUserInfoUri(),
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String platformId = root.path("response").get("id").asText();
            String email = root.path("response").get("email").asText();

            return UserInfoDto.of(platformId, email);
        } catch (Exception e) {
            log.error("[OAUTH ERROR:naver] {}", e.getMessage());
            throw new CustomException(OAuthErrorType.OAUTH_ERROR);
        }
    }
}