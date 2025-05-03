package kr.co.yeogiga.presentation.auth.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.service.OAuthManagementService;
import kr.co.yeogiga.application.auth.type.Device;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.util.CookieUtil;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.presentation.auth.api.OAuthApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import static kr.co.yeogiga.application.auth.constant.AuthConstants.REFRESH_TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthController implements OAuthApi {
    private final OAuthManagementService oAuthManagementService;

    @Override
    @PostMapping("/sign-in/{platform}")
    public ResponseEntity<?> signIn(
            @RequestHeader(value = "device") Device device,
            @PathVariable(name = "platform") OAuthPlatform platform,
            @Valid @RequestBody SignInDto.OAuthRequest request) {
        return createSignInResponse(device, oAuthManagementService.signIn(platform, request.code()));
    }


    @PutMapping("/register")
    public ResponseEntity<?> register(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SignUpDto.Register request) {
        oAuthManagementService.register(userDetails.getUserId(), request);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    private ResponseEntity<?> createSignInResponse(Device device, SignInDto.Response response) {
        return switch (device) {
            case MOBILE -> ResponseEntity.ok(SuccessResponse.from(response));
            case WEB -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(
                                REFRESH_TOKEN_PREFIX,
                                response.token().refreshToken(),
                                Duration.ofDays(7).toSeconds()).toString())
                        .body(SuccessResponse.from(response.toWebResponse()));
        };
    }
}

