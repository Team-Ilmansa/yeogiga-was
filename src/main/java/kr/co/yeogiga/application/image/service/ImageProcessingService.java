package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.image.dto.ImageMetadataDto;
import kr.co.yeogiga.application.image.dto.ImageUploadRequest;
import kr.co.yeogiga.infrastructure.s3.AwsS3Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessingService {
    private final ImageMetadataService imageMetadataService;
    private final AwsS3Storage awsS3Storage;

    /**
     * 이미지 업로드 및 메타데이터 추출을 비동기로 처리하는 메서드.
     * - 입력 받은 이미지 정보를 기반으로 메타데이터를 추출
     * - 이미지를 S3에 업로드
     *
     * @param imageUploadRequest 업로드할 이미지 및 관련 정보가 담긴 DTO
     */
    @Async
    public void processImage(ImageUploadRequest imageUploadRequest) {
        try {
            ImageMetadataDto metadata = imageMetadataService.extractMetadata(
                    imageUploadRequest.bytes(), imageUploadRequest.originalFilename()
            );

            String url = awsS3Storage.upload(imageUploadRequest);

            log.info("Processed image - tripId: {}, filename: {}", imageUploadRequest.tripId(), imageUploadRequest.originalFilename());
            log.info("Metadata - latitude: {}, longitude: {}, date: {}", metadata.latitude(), metadata.longitude(), metadata.date());
            log.info("Uploaded URL - {}", url);

        } catch (Exception e) {
            log.error("Error processing image - filename: {}", imageUploadRequest.originalFilename(), e);
        }
    }
}
