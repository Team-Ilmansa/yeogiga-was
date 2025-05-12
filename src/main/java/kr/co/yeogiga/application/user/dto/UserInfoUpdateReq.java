package kr.co.yeogiga.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserInfoUpdateReq(
        @Schema(description = "닉네임", example = "nickname")
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname,

        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email
) {
}