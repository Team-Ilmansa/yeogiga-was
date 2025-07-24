package kr.co.yeogiga.application.notice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class NoticeReq {
    
    @Builder
    public record Creation(
            @NotBlank(message = "제목은 필수 입력값입니다.")
            String title,
            
            @NotBlank(message = "내용은 필수 입력값입니다.")
            String description
    ) {
    }
}
