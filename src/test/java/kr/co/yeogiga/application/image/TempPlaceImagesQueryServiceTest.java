package kr.co.yeogiga.application.image;

import kr.co.yeogiga.application.image.dto.TempImageDto;
import kr.co.yeogiga.application.image.service.TempPlaceImagesQueryService;
import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.TempPlaceImages;
import kr.co.yeogiga.domain.tripplace.service.TempPlaceImagesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TempPlaceImagesQueryServiceTest {

    @Mock
    private TempPlaceImagesService tempPlaceImagesService;

    @InjectMocks
    private TempPlaceImagesQueryService tempPlaceImagesQueryService;

    private final String tripDayPlaceId = "day-id";
    private final String imageId = "image-id";

    @Test
    @DisplayName("임시 저장된 이미지 조회 테스트 - 임시 저장된 이미지가 있을 경우")
    void getTempImagesInfoTest() {
        // given
        Image image = Image.builder()
                .url("https://image.com")
                .build();
        ReflectionTestUtils.setField(image, "id", imageId);

        TempPlaceImages tempPlaceImages = TempPlaceImages.builder()
                .tripDayPlaceId(tripDayPlaceId)
                .build();
        tempPlaceImages.getImages().add(image);

        given(tempPlaceImagesService.readByTripDayPlaceId(tripDayPlaceId))
                .willReturn(Optional.of(tempPlaceImages));

        // when
        List<TempImageDto> result = tempPlaceImagesQueryService.getTempImagesInfo(tripDayPlaceId);

        // then
        assertThat(result).hasSize(1);
        assertEquals(imageId, result.get(0).id());
        assertEquals("https://image.com", result.get(0).url());
    }

    @Test
    @DisplayName("임시 저장된 이미지 조회 테스트 - 임시 저장된 이미지가 없을 경우 (빈 리스트 반환)")
    void getTempImagesInfoReturnsEmptyListTest() {
        // given
        given(tempPlaceImagesService.readByTripDayPlaceId(tripDayPlaceId))
                .willReturn(Optional.empty());

        // when
        List<TempImageDto> result = tempPlaceImagesQueryService.getTempImagesInfo(tripDayPlaceId);

        // then
        assertThat(result).isEmpty();
    }
}
