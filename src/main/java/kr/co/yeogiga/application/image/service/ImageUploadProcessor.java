package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.image.dto.ImageUploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadProcessor {
    private final ImageProcessingService imageProcessingService;

    /**
     * MultipartFile 이미지 리스트를 받아 각각 비동기 업로드 및 메타데이터 추출을 요청메서드
     * 스프링에서 MultipartFile의 경우 요청 범위 내에서만 유효하기 비동기 처리를 위해서 데이터 추출
     *
     * @param images : 업로드 대상 이미지 리스트
     * @param tripId : 이미지가 속한 여행 ID
     */
    public void process(List<MultipartFile> images, Long tripId) {
        for (MultipartFile image : images) {
            try {
                ImageUploadRequest imageUploadRequest = ImageUploadRequest.from(image, tripId);
                imageProcessingService.processImageUpload(imageUploadRequest);
            } catch (IOException e) {
                log.error("Failed to process image - filename: {}", image.getOriginalFilename(), e);
            }
        }
    }
}
