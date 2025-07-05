package kr.co.yeogiga.presentation.auth.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.auth.dto.VerificationCodeDto;
import kr.co.yeogiga.application.auth.service.VerificationCodeService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email-verification")
public class VerificationController {
    private final VerificationCodeService verificationCodeService;
    
    @PostMapping("/request")
    public ResponseEntity<?> sendEmailVerificationCode(@Valid @RequestBody VerificationCodeDto.SendRequest request) {
        verificationCodeService.issueCode(request.email());
        return ResponseEntity.ok(SuccessResponse.ok());
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmailVerificationCode(@Valid @RequestBody VerificationCodeDto.VerificationDto request) {
        verificationCodeService.verifyCode(request.email(), request.code());
        return ResponseEntity.ok(SuccessResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message("인증에 성공하였습니다.")
                        .build());
    }
}
