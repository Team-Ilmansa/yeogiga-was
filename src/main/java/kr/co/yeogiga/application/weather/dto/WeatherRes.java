package kr.co.yeogiga.application.weather.dto;

import kr.co.yeogiga.infrastructure.weather.dto.WeatherItemDto;

public record WeatherRes(
        String category,
        String fcstValue
) {
    public static WeatherRes from(WeatherItemDto dto) {
        return new WeatherRes(dto.category(), dto.fcstValue());
    }
}
