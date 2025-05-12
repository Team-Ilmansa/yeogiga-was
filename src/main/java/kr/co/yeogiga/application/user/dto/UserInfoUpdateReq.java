package kr.co.yeogiga.application.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserInfoUpdateReq(
        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname,

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email
) {
}