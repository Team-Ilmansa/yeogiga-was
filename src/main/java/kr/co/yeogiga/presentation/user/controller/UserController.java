package kr.co.yeogiga.presentation.user.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.application.user.service.UserManagementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.util.CookieUtil;
import kr.co.yeogiga.presentation.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.co.yeogiga.application.auth.constant.AuthConstants.REFRESH_TOKEN_PREFIX;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final UserManagementService userManagementService;

    @Override
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateReq passwordUpdateReq
    ) {
        userManagementService.updatePassword(userDetails.getUserId(), passwordUpdateReq);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @Override
    @DeleteMapping
    public ResponseEntity<?> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userManagementService.withdraw(userDetails.getUserId());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, CookieUtil.removeCookie(REFRESH_TOKEN_PREFIX).toString())
                .body(SuccessResponse.ok());
    }

    @Override
    @GetMapping("/my")
    public ResponseEntity<?> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(SuccessResponse.from(userManagementService.getUserInfo(userDetails.getUserId())));
    }
}
