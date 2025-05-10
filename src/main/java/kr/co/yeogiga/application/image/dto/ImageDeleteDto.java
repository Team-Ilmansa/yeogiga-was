package kr.co.yeogiga.application.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "ImageDeleteDto", description = "임시 이미지 삭제 DTO")
public record ImageDeleteDto(
        @Schema(description = "삭제할 이미지 ID 리스트", example = "[\"image1-id\", \"image2-id\", \"image3-id\"]")
        List<String> imageIds,

        @Schema(description = "삭제할 이미지 url 리스트", example = "[\"https://image1.com\", \"https://image2.com\", \"https://image3.com\"]")
        List<String> urls
) {
}
