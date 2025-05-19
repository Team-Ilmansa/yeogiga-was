package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.application.image.service.ImageDeleteProcessor;
import kr.co.yeogiga.application.tripplace.image.dto.FavoriteImageReq;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripPlaceImageCommandServiceTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @Mock
    private ImageDeleteProcessor imageDeleteProcessor;

    @InjectMocks
    private TripPlaceImageCommandService tripPlaceImageCommandService;

    private final String tripDayPlaceId = "day-1";
    private final String placeId = "place-1";
    private final String imageId = "image-123";

    @Test
    @DisplayName("이미지 즐겨찾기 테스트")
    void updateImageFavoriteStatusTest() {
        // given
        FavoriteImageReq favoriteImageReq = new FavoriteImageReq(placeId, true);

        // when
        tripPlaceImageCommandService.updateImageFavoriteStatus(tripDayPlaceId, imageId, favoriteImageReq);

        // then
        verify(tripDayPlaceService, times(1))
                .updateImageFavorite(tripDayPlaceId, placeId, imageId, true);
    }

    @Nested
    @DisplayName("단일 이미지 삭제 테스트")
    class DeleteSingleImageTest {

        @Test
        @DisplayName("성공 - PLACE 타입")
        void deletePlaceImageSuccess() {
            // given
            TripPlaceImageDeleteDto.SingleDeleteReq req =
                    new TripPlaceImageDeleteDto.SingleDeleteReq(
                            "https://image.com", TripPlaceImageDeleteDto.DeleteType.PLACE, placeId
                    );

            // when
            tripPlaceImageCommandService.deleteSingleImage(tripDayPlaceId, imageId, req);

            // then
            verify(tripDayPlaceService, times(1)).deleteImage(tripDayPlaceId, placeId, imageId);
            verify(imageDeleteProcessor, times(1)).process(List.of(req.url()));
        }

        @Test
        @DisplayName("성공 - UNMATCHED 타입")
        void deleteUnmatchedImageSuccess() {
            // given
            TripPlaceImageDeleteDto.SingleDeleteReq req =
                    new TripPlaceImageDeleteDto.SingleDeleteReq(
                            "https://image.com", TripPlaceImageDeleteDto.DeleteType.UNMATCHED, null
                    );

            // when
            tripPlaceImageCommandService.deleteSingleImage(tripDayPlaceId, imageId, req);

            // then
            verify(tripDayPlaceService, times(1)).deleteImageFromUnMatched(tripDayPlaceId, imageId);
            verify(imageDeleteProcessor, times(1)).process(List.of(req.url()));
        }
    }

    @Test
    @DisplayName("다중 이미지 삭제 성공")
    void deleteMultipleSuccess() {
        // given
        Long tripId = 1L;
        List<String> imageIds = List.of("img1", "img2", "img3");
        List<String> urls = List.of("https://image1.com", "https://image2.com", "https://image3.com");
        TripPlaceImageDeleteDto.MultiDeleteReq req =
                new TripPlaceImageDeleteDto.MultiDeleteReq(imageIds, urls);

        // when
        tripPlaceImageCommandService.deleteMultipleImages(tripId, req);

        // then
        verify(tripDayPlaceService, times(1)).deleteImagesByTripId(tripId, imageIds);
        verify(imageDeleteProcessor, times(1)).process(req.urls());
    }
}
