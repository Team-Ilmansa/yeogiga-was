package kr.co.yeogiga.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "weather")
public class WeatherProperties {
    private String baseUrl;
    private String path;
    private String serviceKey;
    private String pageNo;
    private String numOfRows;
    private String dataType;
}
