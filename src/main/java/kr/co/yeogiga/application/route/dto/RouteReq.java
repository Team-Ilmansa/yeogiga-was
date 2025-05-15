package kr.co.yeogiga.application.route.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class RouteReq {

    @Schema(name = "RouteReq.Request", description = "방장 루트 DTO")
    public record Request(
            @Schema(description = "방장 위치 위도", example = "11.11")
            double latitude,
            @Schema(description = "방장 위치 경도", example = "22.22")
            double longitude
    ) {
        public StoredFormat toStoredFormat() {
            return new StoredFormat(latitude, longitude, LocalDateTime.now());
        }
    }

    public record StoredFormat(
            double latitude,
            double longitude,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @JsonDeserialize(using = LocalDateTimeDeserializer.class)
            @JsonSerialize(using = LocalDateTimeSerializer.class)
            LocalDateTime time
    ) {
    }
}
