package kr.co.yeogiga.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerificationCodeDto {
    
    public record SendRequest(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "잘못된 이메일 형식입니다.")
            String email
    ) { }
    
    public record VerificationDto(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "잘못된 이메일 형식입니다.")
            String email,
            
            @NotBlank(message = "인증 코드는 필수 입력값입니다.")
            String code
    ) { }
}
