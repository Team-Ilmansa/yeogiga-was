package kr.co.yeogiga.application.image.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageUploadRequest {

    @Builder
    public record AwsUploadInfo(
            byte[] bytes,
            String originalFilename,
            String contentType,
            long size,
            Long id
    ) { }

    public record ProfileImage(
            byte[] bytes,
            String originalFilename,
            String contentType,
            long size,
            Long userId
    ) {
        public static ProfileImage from(MultipartFile file, Long userId) throws IOException {
            return new ProfileImage(
                    file.getBytes(), file.getOriginalFilename(),
                    file.getContentType(), file.getSize(),
                    userId
            );
        }

        public AwsUploadInfo toAwsUploadInfo() {
            return AwsUploadInfo.builder()
                    .bytes(bytes)
                    .originalFilename(originalFilename)
                    .contentType(contentType)
                    .size(size)
                    .id(userId)
                    .build();
        }
    }

    public record TripImage(
            byte[] bytes,
            String originalFilename,
            String contentType,
            long size,
            Long tripId,
            String tripDayPlaceId
    ) {
        public static TripImage from(MultipartFile file, Long tripId, String tripDayPlaceId) throws IOException {
            return new TripImage(
                    file.getBytes(), file.getOriginalFilename(),
                    file.getContentType(), file.getSize(),
                    tripId, tripDayPlaceId
            );
        }

        public AwsUploadInfo toAwsUploadInfo() {
            return AwsUploadInfo.builder()
                    .bytes(bytes)
                    .originalFilename(originalFilename)
                    .contentType(contentType)
                    .size(size)
                    .id(tripId)
                    .build();
        }
    }

    public enum ImageType {
        TRIP, PROFILE
    }
}