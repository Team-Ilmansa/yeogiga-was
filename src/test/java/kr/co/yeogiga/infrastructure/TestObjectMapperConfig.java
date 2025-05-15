package kr.co.yeogiga.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.domain.triproute.converter.RouteListConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    public RouteListConverter routeListConverter(ObjectMapper objectMapper) {
        return new RouteListConverter(objectMapper);
    }
}
