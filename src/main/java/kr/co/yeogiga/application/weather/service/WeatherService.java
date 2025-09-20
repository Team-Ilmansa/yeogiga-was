package kr.co.yeogiga.application.weather.service;

import kr.co.yeogiga.application.weather.dto.WeatherRes;
import kr.co.yeogiga.common.util.ForecastTimeUtil;
import kr.co.yeogiga.infrastructure.weather.WeatherClient;
import kr.co.yeogiga.infrastructure.weather.dto.WeatherItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherClient weatherClient;

    /**
     * 해당 격자 공간(nx, ny)에서 현재 시각에 가까운(올림) 시간의 날씨를 조회하는 메서드
     *
     * @param nx 격자 x
     * @param ny 격자 y
     * @return 해당 격자 공간에 대한 날씨 예측 정보
     */
    public List<WeatherRes> getRoundedForecast(int nx, int ny) {
        LocalDateTime now = LocalDateTime.now();

        // 조회 기준 날짜 및 시간 (현재보다 작고 가장 가까운 시각)
        String[] base = ForecastTimeUtil.calcBaseDateTime(now);
        String baseDate = base[0];
        String baseTime = base[1];

        // 날씨를 조회하고자 하는 시간 (현재 시각 올림)
        String[] target = ForecastTimeUtil.calcTargetFcst(now);
        String targetDate = target[0];
        String targetTime = target[1];

        List<WeatherItemDto> items = weatherClient.callVillageFcst(baseDate, baseTime, nx, ny);

        // 조회하고자 하는 시간을 필터링하여 반환
        return items.stream()
                .filter(it -> targetDate.equals(it.fcstDate())
                        && targetTime.equals(it.fcstTime()))
                .map(WeatherRes::from)
                .toList();
    }
}
