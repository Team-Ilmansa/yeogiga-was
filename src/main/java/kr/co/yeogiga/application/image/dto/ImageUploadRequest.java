package kr.co.yeogiga.application.image.dto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public record ImageUploadRequest(
        byte[] bytes,
        String originalFilename,
        String contentType,
        long size,
        Long tripId,
        String tripDayPlaceId
) {
    public static ImageUploadRequest from(MultipartFile file, Long tripId, String tripDayPlaceId) throws IOException {
        return new ImageUploadRequest(
                file.getBytes(), file.getOriginalFilename(),
                file.getContentType(), file.getSize(),
                tripId, tripDayPlaceId
        );
    }
}
