package kr.co.yeogiga.presentation.auth.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

import static kr.co.yeogiga.application.auth.constant.AuthConstants.REFRESH_TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {
    private final AuthService authService;

    @Override
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto.Request request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @Override
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @RequestHeader(name = "device") Device device,
            @Valid @RequestBody SignInDto.Request request
    ) {
        TokenDto token = authService.signIn(request);
        return createTokenResponse(device, token);
    }

    @Override
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
            case MOBILE -> createTokenResponse(device, authService.reissueToken(refreshTokenInHeader));
            case WEB -> createTokenResponse(device, authService.reissueToken(refreshTokenInCookie));
        };
    }

    @Override
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

    @GetMapping("/dup-check/username")
    public ResponseEntity<?> checkDuplicatedUsername(@RequestParam(name = "value") String username) {
        authService.checkDuplicatedUsername(username);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .message("사용 가능한 아이디입니다.")
                .build());
    }

    @GetMapping("/dup-check/nickname")
    public ResponseEntity<?> checkDuplicatedNickname(@RequestParam(name = "value") String nickname) {
        authService.checkDuplicatedNickname(nickname);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .code(HttpStatus.OK.value())
                .message("사용 가능한 닉네임입니다.")
                .build());
    }

    /**
     * 토큰 응답 생성 메서드
     *
     * @param device            사용자 장치(WEB, MOBILE)
     * @param token             토큰(accessToken, refreshToken)
     * @return                  MOBILE -> accessToken(body), refreshToken(body)
     *                          WEB    -> accessToken(body), refreshToken(cookie)
     */
    private ResponseEntity<?> createTokenResponse(Device device, TokenDto token) {
        return switch (device) {
            case MOBILE -> ResponseEntity.ok(SuccessResponse.from(token));
            case WEB -> ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(
                            REFRESH_TOKEN_PREFIX,
                            token.refreshToken(),
                            Duration.ofDays(7).toSeconds()).toString()
                    ).body(SuccessResponse.from(Map.of("accessToken", token.accessToken())));
        };
    }
}
