package kr.co.yeogiga.domain.trip.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@Converter
@RequiredArgsConstructor
public class CityConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper;
    
    @Override
    public String convertToDatabaseColumn(List<String> cities) {
        if (cities == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(cities);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[Error] Failed to convert List to String", e);
        }
    }
    
    @Override
    public List<String> convertToEntityAttribute(String cities) {
        if (cities == null || cities.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            return objectMapper.readValue(cities, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[Error] Failed to convert String to List", e);
        }
    }
}
