package kr.co.yeogiga.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "PasswordUpdateReq", description = "비밀번호 갱신 요청 dto")
public record PasswordUpdateReq (

        @Schema(description = "비밀번호", example = "password")
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
}
