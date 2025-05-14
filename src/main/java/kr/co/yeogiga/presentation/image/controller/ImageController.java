package kr.co.yeogiga.presentation.image.controller;

import kr.co.yeogiga.application.image.dto.ImageDeleteDto;
import kr.co.yeogiga.application.image.service.ImageUploadProcessor;
import kr.co.yeogiga.application.image.service.TempImageAssignProcessor;
import kr.co.yeogiga.application.image.service.TempPlaceImagesCommandService;
import kr.co.yeogiga.application.image.service.TempPlaceImagesQueryService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.presentation.image.api.ImageApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final TempImageAssignProcessor tempImageAssignProcessor;
    private final TempPlaceImagesCommandService tempPlaceImagesCommandService;
    private final TempPlaceImagesQueryService tempPlaceImagesQueryService;

    @Override
    @PostMapping("/{tripId}/day-place/{tripDayPlaceId}/images")
    public ResponseEntity<?> uploadImages(@RequestPart(value = "images", required = false) List<MultipartFile> images,
                                          @PathVariable Long tripId,
                                          @PathVariable String tripDayPlaceId) {
        imageUploadProcessor.process(images, tripId, tripDayPlaceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.created());
    }

    @Override
    @PostMapping("/{tripId}/day-place/{tripDayPlaceId}/images/assign")
    public ResponseEntity<?> assignImages(@PathVariable Long tripId,
                                          @PathVariable String tripDayPlaceId) {
        tempImageAssignProcessor.assignFromTempStorage(tripDayPlaceId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }

    @GetMapping("/{tripId}/day-place/{tripDayPlaceId}/temp-images")
    public ResponseEntity<?> getTempImagesInfo(@PathVariable Long tripId,
                                               @PathVariable String tripDayPlaceId) {
        return ResponseEntity.ok(
                SuccessResponse.from(tempPlaceImagesQueryService.getTempImagesInfo(tripDayPlaceId))
        );
    }

    @Override
    @DeleteMapping("/{tripId}/day-place/temp-images/{tempPlaceImageId}")
    public ResponseEntity<?> removeTempImages(@PathVariable Long tripId,
                                              @PathVariable String tempPlaceImageId,
                                              @RequestBody ImageDeleteDto deleteDto) {
        tempPlaceImagesCommandService.removeTempImages(tempPlaceImageId, deleteDto);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
