package kr.co.yeogiga.common.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static ResponseCookie createCookie(String key, String value, long maxAge) {
        return ResponseCookie.from(key, value)
                .path("/")
                .httpOnly(true)
                .maxAge(maxAge)
                .secure(true)
                .sameSite("None")
                .build();
    }
}