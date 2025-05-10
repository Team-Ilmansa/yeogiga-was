package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.infrastructure.s3.AwsS3Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageDeleteProcessor {
    private final AwsS3Storage awsS3Storage;

    /**
     * 이미지 삭제를 비동기로 처리 메서드
     *
     * @param imageUrls : 이미지 url List
     */
    @Async
    public void process(List<String> imageUrls) {
        for (String url : imageUrls) {
            awsS3Storage.deleteImage(url);
        }
    }
}
