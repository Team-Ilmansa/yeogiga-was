package kr.co.yeogiga.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForecastTimeUtil {
    private static final int[] BASE_HOURS = {2, 5, 8, 11, 14, 17, 20, 23};

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 날씨 요청 기준 날짜 및 시간 계산
     */
    public static String[] calcBaseDateTime(LocalDateTime now) {
        LocalDate date = now.toLocalDate();
        int hour = now.getHour();

        int chosenHour = 23;
        if (hour < 2) {
            date = date.minusDays(1);
        } else {
            for (int h : BASE_HOURS) {
                if (hour < h) break;
                chosenHour = h;
            }
        }

        return new String[]{
                date.format(DATE),
                String.format("%02d00", chosenHour)
        };
    }

    /**
     * 날씨 예측을 위해 현재시간 대비 날짜 및 시간 계산
     */
    public static String[] calcTargetFcst(LocalDateTime now) {
        LocalDate date = now.toLocalDate();
        int hour = now.getHour();
        int minute = now.getMinute();

        if (minute > 0) {
            hour++;
            if (hour == 24) {
                hour = 0;
                date = date.plusDays(1);
            }
        }

        return new String[]{
                date.format(DATE),
                String.format("%02d00", hour)
        };
    }
}
