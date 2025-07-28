package kr.co.yeogiga.presentation.auth.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.service.OAuthManagementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
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
    @PostMapping("/sign-in/{platform}/web")
    public ResponseEntity<?> signIn(
            @PathVariable(name = "platform") OAuthPlatform platform,
            @Valid @RequestBody SignInDto.OAuthRequest.Web request
    ) {
        SignInDto.Response response = oAuthManagementService.signInOnWeb(platform, request.code());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.createCookie(
                        REFRESH_TOKEN_PREFIX,
                        response.token().refreshToken(),
                        Duration.ofDays(7).toSeconds()).toString())
                .body(SuccessResponse.from(response.toWebResponse()));
    }
    
    @Override
    @PostMapping("/sign-in/{platform}/mobile")
    public ResponseEntity<?> signIn(
            @PathVariable(name = "platform") OAuthPlatform platform,
            @Valid @RequestBody SignInDto.OAuthRequest.Mobile request
    ) {
        SignInDto.Response response = oAuthManagementService.signInOnMobile(platform, request.accessToken());
        
        return ResponseEntity.ok(SuccessResponse.from(response));
    }

    @Override
    @PutMapping("/register")
    public ResponseEntity<?> register(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody SignUpDto.Register request) {
        oAuthManagementService.register(userDetails.getUserId(), request);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}

