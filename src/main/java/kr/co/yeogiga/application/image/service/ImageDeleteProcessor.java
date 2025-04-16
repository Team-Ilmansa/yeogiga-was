package kr.co.yeogiga.application.image.service;

import kr.co.yeogiga.application.image.dto.ImageUrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageDeleteProcessor {
    private final ImageProcessingService imageProcessingService;

    public void process(ImageUrlDto imageUrlDto) {
        imageProcessingService.processImageDeletion(imageUrlDto.urls());
    }
}
