package kr.co.yeogiga.presentation.auth.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.service.OAuthManagementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.presentation.auth.api.OAuthApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/oauth")
public class OAuthController implements OAuthApi {
    private final OAuthManagementService oAuthManagementService;

    @PostMapping("/sign-in/{platform}")
    public ResponseEntity<?> signUp(@PathVariable OAuthPlatform platform, @Valid @RequestBody SignInDto.OAuthRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.from(oAuthManagementService.signIn(platform, request.code())));
    }
}

