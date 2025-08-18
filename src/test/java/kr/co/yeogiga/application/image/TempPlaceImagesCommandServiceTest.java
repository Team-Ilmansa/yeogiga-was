package kr.co.yeogiga.application.image;

import kr.co.yeogiga.application.image.dto.ImageDeleteDto;
import kr.co.yeogiga.application.image.service.ImageDeleteProcessor;
import kr.co.yeogiga.application.image.service.TempPlaceImagesCommandService;
import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.tripplace.service.TempPlaceImagesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TempPlaceImagesCommandServiceTest {

    @Mock
    private TempPlaceImagesService tempPlaceImagesService;

    @Mock
    private ImageDeleteProcessor imageDeleteProcessor;

    @InjectMocks
    private TempPlaceImagesCommandService tempPlaceImagesCommandService;

    private final String tripDayPlaceId = "tripDayPlace-id";

    @Test
    @DisplayName("여행 일차에 이미지 임시 저장 테스트")
    void addImageToTripDayPlaceTest() {
        // given
        Image image = Image.builder().url("https://image.com").build();

        // when
        tempPlaceImagesCommandService.addImageToTripDayPlace(tripDayPlaceId, image);

        // then
        verify(tempPlaceImagesService, times(1)).saveImage(tripDayPlaceId, image);
    }

    @Test
    @DisplayName("여행 일차에서의 임시 이미지 삭제 테스트")
    void removeTempImagesTest() {
        // given
        List<String> imageIds = List.of("image1-id", "image2-id");
        List<String> urls = List.of("https://image1.com", "https://image2.com");

        ImageDeleteDto deleteDto = new ImageDeleteDto(imageIds, urls);

        // when
        tempPlaceImagesCommandService.removeTempImages(tripDayPlaceId, deleteDto);

        // then
        verify(tempPlaceImagesService, times(1)).deleteImages(tripDayPlaceId, deleteDto.imageIds());
        verify(imageDeleteProcessor, times(1)).process(deleteDto.urls());
    }
}
