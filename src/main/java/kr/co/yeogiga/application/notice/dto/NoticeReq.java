package kr.co.yeogiga.application.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class NoticeReq {
    
    @Builder
    @Schema(name = "NoticeReq.Creation", description = "여행 생성 요청 DTO")
    public record Creation(
            @Schema(description = "제목", example = "필수 준비물")
            @NotBlank(message = "제목은 필수 입력값입니다.")
            String title,
            
            @Schema(description = "내용", example = "준비물 다시 확인하시고 꼭 챙기십시오.")
            @NotBlank(message = "내용은 필수 입력값입니다.")
            String description
    ) {
    }
}
