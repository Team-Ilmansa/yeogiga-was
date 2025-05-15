package kr.co.yeogiga.application.route.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class RouteReq {

    public record Request(
            double latitude,
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
