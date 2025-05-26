package kr.co.yeogiga.application.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FcmTokenReq", description = "FcmToken 갱신 요청 dto")
public record FcmTokenReq(
        @Schema(description = "FcmToken 값", example = "fcm-token")
        String fckToken
) {
}
