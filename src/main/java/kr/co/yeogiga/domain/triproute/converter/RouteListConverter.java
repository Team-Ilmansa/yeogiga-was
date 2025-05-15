package kr.co.yeogiga.domain.triproute.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.co.yeogiga.domain.triproute.entity.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Converter
@Component
@RequiredArgsConstructor
public class RouteListConverter implements AttributeConverter<List<Route>, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<Route> dataList) {
        try {
            return objectMapper.writeValueAsString(dataList);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize list to JSON", e);
        }
    }

    @Override
    public List<Route> convertToEntityAttribute(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to list", e);
        }
    }
}
