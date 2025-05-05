package kr.co.yeogiga.application.user.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateReq (
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
}
