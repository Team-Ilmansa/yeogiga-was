package kr.co.yeogiga.presentation.auth.controller;

import kr.co.yeogiga.application.auth.dto.OAuthSignInDto;
import kr.co.yeogiga.application.auth.service.OAuthManagementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/oauth")
public class OAuthController {
    private final OAuthManagementService oAuthManagementService;

    @PostMapping("/sign-in/{platform}")
    public ResponseEntity<?> signUp(@PathVariable OAuthPlatform platform, @RequestBody OAuthSignInDto.Request request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.from(oAuthManagementService.signIn(platform, request.code())));
    }
}

