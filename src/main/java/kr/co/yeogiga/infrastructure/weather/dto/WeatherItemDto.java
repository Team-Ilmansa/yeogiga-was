package kr.co.yeogiga.infrastructure.weather.dto;

public record WeatherItemDto(
        String fcstDate,
        String fcstTime,
        String category,
        String fcstValue
) {
}
