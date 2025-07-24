package kr.co.yeogiga.application.notice.dto;

import lombok.Builder;

public class NoticeReq {
    
    @Builder
    public record Creation(
            String title,
            String description
    ) {
    }
}
