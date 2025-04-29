package kr.co.yeogiga.presentation.auth.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.service.AuthService;
import kr.co.yeogiga.application.auth.type.Device;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.util.CookieUtil;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.presentation.auth.api.AuthApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

import static kr.co.yeogiga.application.auth.constant.AuthConstants.REFRESH_TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {
    private final AuthService authService;

    @GetMapping("/reissue")
    public ResponseEntity<?> reissueToken(
            @RequestHeader(name = "device") Device device,
            @RequestHeader(name = "refreshToken", required = false) String refreshTokenInHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshTokenInCookie
    ) {
        if ((device.equals(Device.WEB) && refreshTokenInCookie == null)
            || (device.equals(Device.MOBILE) && refreshTokenInHeader == null)) {
            throw new CustomException(AuthErrorType.REFRESH_TOKEN_NOT_FOUND);
        }

        return switch (device) {
            case MOBILE -> createReissueTokenResponse(device, authService.reissueToken(refreshTokenInHeader));
            case WEB -> createReissueTokenResponse(device, authService.reissueToken(refreshTokenInCookie));
        };
    }

    @GetMapping("/sign-out")
    public ResponseEntity<?> signOut(
            @RequestHeader(name = "device") Device device,
            @RequestHeader(name = "refreshToken", required = false) String refreshTokenInHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshTokenInCookie
    ) {
        if (device.equals(Device.WEB) && refreshTokenInCookie != null) {
            authService.signOut(refreshTokenInCookie);
        }

        if (device.equals(Device.MOBILE) && refreshTokenInHeader != null) {
            authService.signOut(refreshTokenInHeader);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.removeCookie(REFRESH_TOKEN_PREFIX).toString())
                .body(SuccessResponse.ok());
    }

    /**
     * 토큰 재발급 시 HTTP 응답 생성 메서드
     *
     * @param device            사용자 장치(WEB, MOBILE)
     * @param reissuedToken     신규 발급 토큰(accessToken, refreshToken)
     * @return                  MOBILE -> accessToken(body), refreshToken(body)
     *                          WEB    -> accessToken(body), refreshToken(cookie)
     */
    private ResponseEntity<?> createReissueTokenResponse(Device device, TokenDto reissuedToken) {
        return switch (device) {
            case MOBILE -> ResponseEntity.ok(SuccessResponse.from(reissuedToken));
            case WEB -> ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(
                            REFRESH_TOKEN_PREFIX,
                            reissuedToken.refreshToken(),
                            Duration.ofDays(7).toSeconds()).toString()
                    ).body(SuccessResponse.from(Map.of("accessToken", reissuedToken.accessToken())));
        };
    }
}
