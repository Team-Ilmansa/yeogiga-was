package kr.co.yeogiga.application.image.dto;

import java.time.LocalDateTime;

public record ImageMetadataDto(
        Double latitude,
        Double longitude,
        LocalDateTime date
) {
    public static ImageMetadataDto of(Double latitude, Double longitude, LocalDateTime date) {
        return new ImageMetadataDto(latitude, longitude, date);
    }
}
