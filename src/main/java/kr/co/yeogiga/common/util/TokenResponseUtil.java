package kr.co.yeogiga.common.util;

import com.google.common.net.HttpHeaders;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.type.Device;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.Map;

import static kr.co.yeogiga.application.auth.constant.AuthConstants.REFRESH_TOKEN_PREFIX;

public class TokenResponseUtil {
    
    /**
     * 사용자 접속 디바이스 별 HTTP 응답 객체 생성 유틸 메서드
     *
     * @param device    사용자 디바이스 종류(WEB, MOBILE)
     * @param token     토큰(accessToken, refreshToken)
     * @return          MOBILE  -> accessToken(body), refreshToken(body)
     *                  WEB     -> accessToken(body), refreshToken(Cookie)
     */
    public static ResponseEntity<?> createTokenResponse(Device device, TokenDto token) {
        return switch(device) {
            case MOBILE -> ResponseEntity.ok(SuccessResponse.from(token));
            case WEB -> ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(
                            REFRESH_TOKEN_PREFIX,
                            token.refreshToken(),
                            Duration.ofDays(7).toSeconds()).toString()
                    )
                    .body(SuccessResponse.from(Map.of("accessToken", token.accessToken())));
        };
    }
}
