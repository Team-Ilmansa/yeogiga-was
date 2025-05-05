package kr.co.yeogiga.presentation.image.controller;

import kr.co.yeogiga.application.image.dto.ImageUrlDto;
import kr.co.yeogiga.application.image.service.ImageDeleteProcessor;
import kr.co.yeogiga.application.image.service.ImageUploadProcessor;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.image.api.ImageApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class ImageController implements ImageApi {
    private final ImageUploadProcessor imageUploadProcessor;
    private final ImageDeleteProcessor imageDeleteProcessor;

    @Override
    @PostMapping("/{tripId}/images/{placeId}")
    public ResponseEntity<?> uploadImages(@RequestPart(value = "images", required = false) List<MultipartFile> images,
                                          @PathVariable Long tripId,
                                          @PathVariable String placeId) {
        imageUploadProcessor.process(images, tripId, placeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @Override
    @DeleteMapping("/{tripId}/images")
    public ResponseEntity<?> deleteImage(@RequestBody ImageUrlDto imageUrlDto) {
        imageDeleteProcessor.process(imageUrlDto);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
