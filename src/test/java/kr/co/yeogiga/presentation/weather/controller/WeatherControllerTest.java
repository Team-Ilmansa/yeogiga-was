package kr.co.yeogiga.presentation.weather.controller;

import kr.co.yeogiga.application.weather.dto.WeatherRes;
import kr.co.yeogiga.application.weather.service.WeatherService;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import kr.co.yeogiga.infrastructure.weather.dto.WeatherItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = WeatherController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class WeatherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("날씨 조회 테스트")
    void getWeatherTest() throws Exception {
        // given
        List<WeatherRes> response = List.of(
                WeatherRes.from(new WeatherItemDto("20250920", "0200", "UUU", "-1.9")),
                WeatherRes.from(new WeatherItemDto("20250920", "0200", "VEC", "23"))
        );
        int nx = 55, ny = 127;

        when(weatherService.getRoundedForecast(nx, ny)).thenReturn(response);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/weather")
                        .queryParam("nx", String.valueOf(nx))
                        .queryParam("ny", String.valueOf(ny))
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].category").value(response.get(0).category()))
                .andExpect(jsonPath("$.data[0].fcstValue").value(response.get(0).fcstValue()));
    }
}
