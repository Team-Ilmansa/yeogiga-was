package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.image.dto.ImageUploadRequest;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.tripplace.exception.ImageErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadProcessor {
    private final ImageProcessingService imageProcessingService;

    /**
     * MultipartFile 이미지 리스트를 받아 각각 비동기 업로드 및 메타데이터 추출을 요청메서드
     * 스프링에서 MultipartFile의 경우 요청 범위 내에서만 유효하기 비동기 처리를 위해서 데이터 추출
     *
     * @param images         : 업로드 대상 이미지 리스트
     * @param tripId         : 이미지가 속한 여행 ID
     * @param tripDayPlaceId : 이미지가 속할 여행 일차 ID
     */
    public void process(List<MultipartFile> images, Long tripId, String tripDayPlaceId) {
        for (MultipartFile image : images) {
            try {
                ImageUploadRequest.TripImage imageUploadRequest = ImageUploadRequest.TripImage.from(image, tripId, tripDayPlaceId);
                imageProcessingService.processImageUpload(imageUploadRequest);
            } catch (IOException e) {
                log.error("Failed to process image - filename: {}", image.getOriginalFilename(), e);
            }
        }
    }

    /**
     * 사용자 프로필 MultiPartFile 이미지를 받아 비동기 업로드 처리 메서드
     *
     * @param image         : 업로드 대상 이미지
     * @param userId        : 사용자 ID
     */
    public void uploadProfileImage(MultipartFile image, Long userId) {
        try {
            if (!isValidImage(image)) {
                throw new CustomException(ImageErrorType.IMAGE_REQUIRED);
            }

            ImageUploadRequest.ProfileImage imageUploadRequest = ImageUploadRequest.ProfileImage.from(image, userId);
            imageProcessingService.processProfileImageUpload(imageUploadRequest);
        } catch (IOException e) {
            log.error("Failed to process profile image - filename: {}", image.getOriginalFilename(), e);
        }
    }

    /**
     * 이미지 유효성 검사 메서드
     *
     * @param image         : 이미지 파일
     * @return              : 이미지 유효성 여부
     */
    private boolean isValidImage(MultipartFile image) {
        return Objects.nonNull(image) && !image.getOriginalFilename().isEmpty();
    }
}
