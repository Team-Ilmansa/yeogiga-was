package kr.co.yeogiga.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerificationCodeDto {
    
    @Schema(name = "VerificationDto.SendRequest", description = "이메일 인증 번호 발송 요청 DTO")
    public record SendRequest(
            @Schema(description = "인증 요청 이메일", example = "test@test.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "잘못된 이메일 형식입니다.")
            String email
    ) { }
    
    @Schema(name = "VerificationDto.VerificationRequest", description = "이메일 인증 번호 검증 요청 DTO")
    public record VerificationRequest(
            @Schema(description = "인증 요청 이메일", example = "test@test.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "잘못된 이메일 형식입니다.")
            String email,
            
            @Schema(description = "인증 번호", example = "123456")
            @NotBlank(message = "인증 번호는 필수 입력값입니다.")
            String code
    ) { }
}
