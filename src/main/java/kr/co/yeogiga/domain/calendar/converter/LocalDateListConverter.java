package kr.co.yeogiga.domain.calendar.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Converter
@RequiredArgsConstructor
public class LocalDateListConverter implements AttributeConverter<List<LocalDate>, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<LocalDate> dataList) {
        try {
            return objectMapper.writeValueAsString(dataList);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize list to JSON", e);
        }
    }

    @Override
    public List<LocalDate> convertToEntityAttribute(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to deserialize JSON to list", e);
        }
    }
}
