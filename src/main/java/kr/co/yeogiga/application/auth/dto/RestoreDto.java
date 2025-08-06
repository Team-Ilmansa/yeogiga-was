package kr.co.yeogiga.application.auth.dto;

import jakarta.validation.constraints.NotNull;

public class RestoreDto {
    
    public record Request(
            @NotNull(message = "사용자 ID는 필수 입력값입니다.")
            Long userId
    ) { }
}
