package kr.co.yeogiga.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PasswordResetDto {
    public record Request(
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "잘못된 이메일 형식입니다.")
            String email,
            
            @NotBlank(message = "아이디는 필수 입력값입니다.")
            String username
    ) { }
}
