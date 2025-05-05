package kr.co.yeogiga.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "PasswordUpdateReq", description = "비밀번호 갱신 요청 dto")
public record PasswordUpdateReq (

        @Schema(description = "기존 비밀번호", example = "originalPassword")
        @NotBlank(message = "기존 비밀번호는 필수 입력값입니다.")
        String originalPassword,

        @Schema(description = "새로운 비밀번호", example = "newPassword")
        @NotBlank(message = "새로운 비밀번호는 필수 입력값입니다.")
        String newPassword
) {
}
