package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.image.dto.ImageMetadataDto;
import kr.co.yeogiga.application.image.dto.ImageUploadRequest;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.infrastructure.s3.AwsS3Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageProcessingService {
    private final ImageMetadataService imageMetadataService;
    private final AwsS3Storage awsS3Storage;
    private final TempPlaceImagesCommandService tempPlaceImagesCommandService;

    /**
     * 이미지 업로드 및 메타데이터 추출을 비동기로 처리하는 메서드.
     * - 입력 받은 이미지 정보를 기반으로 메타데이터를 추출
     * - 이미지를 S3에 업로드
     * - 업로드된 이미지 이미지 임시 저장소에 저장
     *
     * @param imageUploadRequest 업로드할 이미지 및 관련 정보가 담긴 DTO
     */
    @Async
    public void processImageUpload(ImageUploadRequest imageUploadRequest) {
        try {
            ImageMetadataDto metadata = imageMetadataService.extractMetadata(
                    imageUploadRequest.bytes(), imageUploadRequest.originalFilename()
            );

            String url = awsS3Storage.upload(imageUploadRequest);

            Image image = Image.builder()
                    .url(url)
                    .latitude(metadata.latitude())
                    .longitude(metadata.longitude())
                    .date(metadata.date())
                    .build();

            tempPlaceImagesCommandService.addImageToPlace(imageUploadRequest.tripDayPlaceId(), image);

        } catch (Exception e) {
            log.error("Error processing image - filename: {}", imageUploadRequest.originalFilename(), e);
        }
    }

    /**
     * 이미지 삭제를 비동기로 처리
     *
     * @param imageUrls : 이미지 url List
     */
    @Async
    public void processImageDeletion(List<String> imageUrls) {
        for (String url : imageUrls) {
            awsS3Storage.deleteImage(url);
        }
    }
}
