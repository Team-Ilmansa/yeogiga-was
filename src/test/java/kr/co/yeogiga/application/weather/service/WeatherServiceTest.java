package kr.co.yeogiga.application.weather.service;

import kr.co.yeogiga.application.weather.dto.WeatherRes;
import kr.co.yeogiga.common.util.ForecastTimeUtil;
import kr.co.yeogiga.infrastructure.weather.WeatherClient;
import kr.co.yeogiga.infrastructure.weather.dto.WeatherItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    @DisplayName("날씨 조회 테스트")
    void getRoundedForecastTest() {
        int nx = 55, ny = 127;

        try (MockedStatic<ForecastTimeUtil> mocked = mockStatic(ForecastTimeUtil.class)) {
            // given
            mocked.when(() -> ForecastTimeUtil.calcBaseDateTime(Mockito.any(LocalDateTime.class)))
                    .thenReturn(new String[]{"20250920", "0200"});
            mocked.when(() -> ForecastTimeUtil.calcTargetFcst(Mockito.any(LocalDateTime.class)))
                    .thenReturn(new String[]{"20250920", "0300"});

            when(weatherClient.callVillageFcst("20250920", "0200", nx, ny))
                    .thenReturn(List.of(
                            new WeatherItemDto("20250920", "0200", "UUU", "-1.9"),
                            new WeatherItemDto("20250920", "0300", "TMP", "18"),
                            new WeatherItemDto("20250920", "0300", "POP", "60")
                    ));

            // when
            List<WeatherRes> result = weatherService.getRoundedForecast(nx, ny);

            // then
            assertThat(result).hasSize(2);
        }
    }
}
