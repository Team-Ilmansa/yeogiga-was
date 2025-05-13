package kr.co.yeogiga.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserInfoUpdateReq(
        @Schema(description = "닉네임", example = "nickname")
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname
) {
}