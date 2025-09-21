package kr.co.yeogiga.infrastructure.weather;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import kr.co.yeogiga.infrastructure.properties.WeatherProperties;
import kr.co.yeogiga.infrastructure.weather.dto.WeatherItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherClient {
    private final ObjectMapper objectMapper;
    private final WeatherProperties weatherProperties;

    /**
     * 기준 날짜 및 시간을 통해 격자 공간에 대한 날씨를 조회하는 메서드
     * - 공공 데이터 포털 open api 사용
     *
     * @param baseDate 기준 날짜
     * @param baseTime 기준 시간
     * @param nx       격자 x
     * @param ny       격자 y
     * @return 해당 격자 공간에 대한 날씨 예측 정보
     */
    public List<WeatherItemDto> callVillageFcst(String baseDate, String baseTime, int nx, int ny) {
        URI uri = UriComponentsBuilder.fromUriString(weatherProperties.getBaseUrl())
                .path(weatherProperties.getPath())
                .queryParam("serviceKey", weatherProperties.getServiceKey())
                .queryParam("pageNo", weatherProperties.getPageNo())
                .queryParam("numOfRows", weatherProperties.getNumOfRows())
                .queryParam("dataType", weatherProperties.getDataType())
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build(true)
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    String.class
            );

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode items = rootNode
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            return objectMapper.readValue(items.traverse(), new TypeReference<>() {
            });

        } catch (Exception e) {
            log.error("[ERROR] Could not fetch forecast from PublicData API - {}", e.getMessage());
            throw new CustomException(CommonErrorType.INTERNAL_SERVER_ERROR);
        }
    }
}
