package kr.co.yeogiga.domain.notice.dto;

import kr.co.yeogiga.domain.notice.entity.Notice;
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
    ) {
        public static Detail fromEntity(Notice notice) {
            return Detail.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .description(notice.getDescription())
                    .createdAt(notice.getCreatedAt())
                    .authorId(notice.getAuthorId())
                    .nickname(notice.getAuthor().getNickname())
                    .imageUrl(notice.getAuthor().getImageUrl())
                    .build();
        }
    }
}
