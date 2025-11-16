package kr.co.yeogiga.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PasswordResetDto {
    public record Request(
            @Schema(description = "이메일", example = "test@test.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "잘못된 이메일 형식입니다.")
            String email,
            
            @Schema(description = "아이디", example = "testid")
            @NotBlank(message = "아이디는 필수 입력값입니다.")
            String username
    ) { }
}
