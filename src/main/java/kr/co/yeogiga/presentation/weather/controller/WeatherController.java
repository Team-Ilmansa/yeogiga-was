package kr.co.yeogiga.presentation.weather.controller;

import kr.co.yeogiga.application.weather.service.WeatherService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<?> getWeather(
            @RequestParam int nx,
            @RequestParam int ny
    ) {
        return ResponseEntity.ok(
                SuccessResponse.from(weatherService.getRoundedForecast(nx, ny))
        );
    }
}
