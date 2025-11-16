package kr.co.yeogiga.presentation.auth.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.auth.dto.PasswordResetDto;
import kr.co.yeogiga.application.auth.service.PasswordManagementService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordManagementService passwordManagementService;
    
    @PostMapping("/password/reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetDto.Request request) {
        passwordManagementService.requestPasswordReset(request.email(), request.username());
        
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
