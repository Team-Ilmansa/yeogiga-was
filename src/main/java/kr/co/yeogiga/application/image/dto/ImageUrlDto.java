package kr.co.yeogiga.application.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ImageUrlDto", description = "삭제할 이미지 url DTO")
public record ImageUrlDto(
        @Schema(description = "삭제할 이미지들", example = "[\"https://example-image1.com\", \"https://example-image2.com\"]")
        List<String> urls
) {
}
