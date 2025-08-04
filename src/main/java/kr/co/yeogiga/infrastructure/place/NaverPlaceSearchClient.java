package kr.co.yeogiga.infrastructure.place;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.infrastructure.place.dto.NaverPlaceInfoDto;
import kr.co.yeogiga.infrastructure.place.dto.PlaceInfoDto;
import kr.co.yeogiga.infrastructure.properties.OAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NaverPlaceSearchClient implements PlaceSearchClient {
    private final String clientId;
    private final String clientSecret;
    private final ObjectMapper objectMapper;
    
    private final String CLIENT_ID_HEADER = "X-Naver-Client-Id";
    private final String CLIENT_SECRET_HEADER = "X-Naver-Client-Secret";
    
    private final String NAVER_OPEN_API_URI = "https://openapi.naver.com/";
    private final String PLACE_SEARCH_PATH = "v1/search/local.json";
    
    @Autowired
    public NaverPlaceSearchClient(OAuthProperties oAuthProperties, ObjectMapper objectMapper) {
        OAuthProperties.Platform naverProperties = oAuthProperties.getNaver();
        
        this.clientId = naverProperties.getClientId();
        this.clientSecret = naverProperties.getClientSecret();
        this.objectMapper = objectMapper;
    }
    
    /**
     * 특정 장소 키워드를 통한 연관된 장소 검색 메서드
     * - 네이버 지역 검색 API 사용
     *
     * @param place     검색할 장소 키워드
     * @return          해당 키워드와 연관된 장소 목록
     */
    @Override
    public List<PlaceInfoDto> fetchPlaces(String place) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CLIENT_ID_HEADER, clientId);
        headers.add(CLIENT_SECRET_HEADER, clientSecret);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        URI uri = UriComponentsBuilder.fromUriString(NAVER_OPEN_API_URI)
                .path(PLACE_SEARCH_PATH)
                .queryParam("query", place)
                .queryParam("display", 5)
                .queryParam("start", 1)
                .queryParam("sort", "random")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();
        
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    String.class
            );
            
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode items = rootNode.get("items");
            
            List<NaverPlaceInfoDto> naverPlaceInfoList = objectMapper.readValue(items.traverse(), new TypeReference<List<NaverPlaceInfoDto>>() {
            });
            
            return new ArrayList<>(naverPlaceInfoList);
            
        } catch (Exception e) {
            log.error("[ERROR] Could not fetch place from NAVER place search api - {}", e.getMessage());
            throw new CustomException(CommonErrorType.INTERNAL_SERVER_ERROR);
        }
    }
}
