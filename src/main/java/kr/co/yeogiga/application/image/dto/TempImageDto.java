package kr.co.yeogiga.application.image.dto;

import kr.co.yeogiga.domain.placeimage.entity.Image;

public record TempImageDto(
        String id,
        String url
) {
    public static TempImageDto from(Image image) {
        return new TempImageDto(image.getId(), image.getUrl());
    }
}
