package kr.co.yeogiga.application.image.dto;

import java.util.List;

public record ImageDeleteDto(
        List<String> imageIds,
        List<String> urls
) {
}
