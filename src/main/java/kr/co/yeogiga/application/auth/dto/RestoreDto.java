package kr.co.yeogiga.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class RestoreDto {
    
    @Schema(name = "RestoreDto.Request", description = "계정 복구 요청 DTO")
    public record Request(
            @Schema(description = "사용자 ID", example = "1")
            @NotNull(message = "사용자 ID는 필수 입력값입니다.")
            Long userId
    ) { }
}
