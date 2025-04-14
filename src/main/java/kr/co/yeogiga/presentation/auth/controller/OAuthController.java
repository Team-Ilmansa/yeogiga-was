package kr.co.yeogiga.presentation.auth.controller;

import kr.co.yeogiga.application.auth.dto.OAuthSignInDto;
import kr.co.yeogiga.application.auth.service.OAuthService;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v0/auth/oauth")
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping("/sign-in/{platform}")
    public ResponseEntity<?> signUp(@PathVariable OAuthPlatform platform, @RequestBody OAuthSignInDto.Request request) {
        oAuthService.signIn(platform, request.code());

        // HACK: JWT 적용 시 응답 수정
        return ResponseEntity.ok().build();
    }
}

