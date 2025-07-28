package kr.co.yeogiga.domain.notice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public class NoticeDto {
    
    @Builder
    public record Detail(
            Long id,
            String title,
            String description,
            LocalDateTime createdAt,
            Long authorId,
            String nickname,
            String imageUrl
    ) { }
}
