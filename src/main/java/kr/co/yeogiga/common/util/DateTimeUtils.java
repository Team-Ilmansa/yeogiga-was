package kr.co.yeogiga.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtils {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    /**
     * Date → LocalDateTime(KST) 변환
     *
     * @param date : java.util.Date 객체
     * @return 변환된 LocalDateTime, null이면 null 반환
     */
    public static LocalDateTime toKoreaLocalDateTime(Date date) {
        if (date == null) return null;
        Instant instant = date.toInstant();
        return instant.atZone(KOREA_ZONE).toLocalDateTime();
    }
}
