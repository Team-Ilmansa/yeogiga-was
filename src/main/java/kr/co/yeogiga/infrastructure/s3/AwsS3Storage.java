package kr.co.yeogiga.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kr.co.yeogiga.application.image.dto.ImageUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AwsS3Storage {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucket;

    /**
     * 이미지 파일을 S3에 업로드
     *
     * @param imageUploadRequest : 업로드할 이미지 정보가 담긴 DTO
     * @return 업로드된 이미지의 S3 URL
     */
    public String upload(
            ImageUploadRequest.AwsUploadInfo imageUploadRequest,
            ImageUploadRequest.ImageType imageType
    ) {
        String fileName = generateFileName(
                imageType,
                imageUploadRequest.originalFilename(),
                imageUploadRequest.id()
        );
        ObjectMetadata metadata = createObjectMetadata(
                imageUploadRequest.contentType(),
                imageUploadRequest.size()
        );

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageUploadRequest.bytes())) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
        } catch (IOException e) {
            throw new IllegalArgumentException("이미지 업로드 중 오류 발생", e);
        }

        return getFileUrl(fileName);
    }

    /**
     * S3에 저장된 이미지를 삭제
     *
     * @param imageUrl : 삭제할 이미지의 전체 URL
     */
    public void deleteImage(String imageUrl) {
        String fileName = extractFileNameFromUrl(imageUrl);
        amazonS3.deleteObject(bucket, fileName);
    }

    /**
     * 업로드할 파일명을 생성
     *
     * @param originalFileName : 원본 파일명
     * @param id               : 이미지가 속한 DB PK값
     * @return S3에 저장할 파일 경로
     */
    private String generateFileName(ImageUploadRequest.ImageType imageType, String originalFileName, Long id) {
        String rootDir = switch (imageType) {
            case TRIP -> "image/";
            case PROFILE -> "profile/";
        };

        return rootDir +  id + "/" + UUID.randomUUID() + "-" + originalFileName;
    }

    /**
     * S3에 업로드할 때 필요한 메타데이터를 생성
     *
     * @param contentType : MIME 타입 (예: image/jpeg)
     * @param size        : 파일 크기 (바이트 단위)
     * @return 생성된 ObjectMetadata
     */
    private ObjectMetadata createObjectMetadata(String contentType, long size) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(size);
        return metadata;
    }

    /**
     * 저장된 S3 파일의 전체 URL을 반환
     *
     * @param fileName : S3 내의 파일 경로
     * @return 전체 접근 가능한 이미지 URL
     */
    private String getFileUrl(String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    /**
     * 이미지 URL에서 S3에 저장된 파일명을 추출
     *
     * @param imageUrl : 전체 S3 URL
     * @return S3 내의 파일 경로 (예: image/...)
     */
    private String extractFileNameFromUrl(String imageUrl) {
        int startIndex = imageUrl.indexOf("/", imageUrl.indexOf("//") + 2);
        return imageUrl.substring(startIndex + 1);
    }
}
