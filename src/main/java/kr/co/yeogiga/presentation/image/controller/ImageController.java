package kr.co.yeogiga.presentation.image.controller;

import kr.co.yeogiga.application.image.service.ImageUploadProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class ImageController {
    private final ImageUploadProcessor imageUploadProcessor;

    @PostMapping("/{tripId}/images")
    public void uploadImages(@RequestPart(value = "images", required = false) List<MultipartFile> images,
                             @PathVariable Long tripId) {
        imageUploadProcessor.process(images, tripId);
    }
}
