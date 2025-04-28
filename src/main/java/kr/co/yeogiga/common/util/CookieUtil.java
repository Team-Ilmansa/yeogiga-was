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

    public static ResponseCookie removeCookie(String key) {
        return ResponseCookie.from(key, "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .build();
    }
}