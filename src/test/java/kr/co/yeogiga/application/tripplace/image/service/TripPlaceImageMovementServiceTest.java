package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDto;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageMovementService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.exception.ImageErrorType;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripPlaceImageMovementServiceTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @InjectMocks
    private TripPlaceImageMovementService imageMovementService;

    private final String tripDayPlaceId1 = "day1";
    private final String tripDayPlaceId2 = "day2";
    private final String fromPlaceId = "place1-id";
    private final String toPlaceId = "place2-id";
    private final String imageId = "image-id";

    private Image buildImage() {
        Image image = Image.builder().url("url").latitude(1.0).longitude(1.0).build();
        ReflectionTestUtils.setField(image, "id", imageId);
        return image;
    }

    private Place buildPlace(String id, String name, List<Image> images) {
        Place place = Place.builder()
                .id(id).name(name).latitude(1.0).longitude(1.0)
                .placeType("카페").order(1)
                .build();
        place.addImages(images);
        return place;
    }

    private TripDayPlace buildTripDayPlace(List<Place> places) {
        return TripDayPlace.builder()
                .tripId(1L).day(1)
                .places(places)
                .build();
    }

    @Nested
    @DisplayName("같은 날짜 다른 목적지 이미지 이동 테스트")
    class MoveImageToAnotherPlaceTest {

        private final TripPlaceImageDto.ImageMoveReq imageReq
                = new TripPlaceImageDto.ImageMoveReq(fromPlaceId, toPlaceId, imageId);

        @Test
        @DisplayName("성공")
        void success() {
            // given
            Image image = buildImage();
            Place from = buildPlace(fromPlaceId, "출발지", List.of(image));
            Place to = buildPlace(toPlaceId, "도착지", List.of());
            TripDayPlace tripDayPlace = buildTripDayPlace(List.of(from, to));

            given(tripDayPlaceService.readById(tripDayPlaceId1)).willReturn(Optional.of(tripDayPlace));

            // when
            imageMovementService.moveImageToAnotherPlace(tripDayPlaceId1, imageReq);

            // then
            verify(tripDayPlaceService, times(1)).deleteImage(tripDayPlaceId1, fromPlaceId, image.getId());
            verify(tripDayPlaceService, times(1)).saveImage(tripDayPlaceId1, toPlaceId, image);
        }

        @Test
        @DisplayName("실패 - TripDayPlace 없음")
        void failNotFoundTripDayPlace() {
            // given
            given(tripDayPlaceService.readById(tripDayPlaceId1)).willReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    imageMovementService.moveImageToAnotherPlace(tripDayPlaceId1, imageReq)
            );

            // then
            assertEquals(TripErrorType.TRIP_PLACE_NOT_FOUND, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - Place 없음")
        void failNotFoundPlace() {
            // given
            TripDayPlace tripDayPlace = buildTripDayPlace(List.of());
            given(tripDayPlaceService.readById(tripDayPlaceId1)).willReturn(Optional.of(tripDayPlace));

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    imageMovementService.moveImageToAnotherPlace(tripDayPlaceId1, imageReq)
            );

            // then
            assertEquals(TripErrorType.PLACE_NOT_FOUND, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 이미지 없음")
        void failNotFoundImage() {
            // given
            Place from = buildPlace(fromPlaceId, "목적지", List.of());
            TripDayPlace tripDayPlace = buildTripDayPlace(List.of(from));
            given(tripDayPlaceService.readById(tripDayPlaceId1)).willReturn(Optional.of(tripDayPlace));

            // when
            CustomException exception = assertThrows(CustomException.class, () ->
                    imageMovementService.moveImageToAnotherPlace(tripDayPlaceId1, imageReq)
            );

            // then
            assertEquals(ImageErrorType.NOT_FOUND, exception.getErrorType());
        }
    }

    @Test
    @DisplayName("다른 날짜 간 이미지 이동 테스트")
    void moveImageBetweenDayPlacesTest() {
        // given
        TripPlaceImageDto.ImageCrossDayMoveReq imageReq =
                new TripPlaceImageDto.ImageCrossDayMoveReq(tripDayPlaceId1, fromPlaceId, tripDayPlaceId2, toPlaceId, imageId);

        Image image = buildImage();
        Place from = buildPlace(fromPlaceId, "목적지1", List.of(image));

        TripDayPlace fromDay = buildTripDayPlace(List.of(from));

        given(tripDayPlaceService.readById(tripDayPlaceId1)).willReturn(Optional.of(fromDay));

        // when
        imageMovementService.moveImageBetweenDayPlaces(imageReq);

        // then
        verify(tripDayPlaceService, times(1)).deleteImage(tripDayPlaceId1, fromPlaceId, imageId);
        verify(tripDayPlaceService, times(1)).saveImage(tripDayPlaceId2, toPlaceId, image);
    }

    @Test
    @DisplayName("이미지 Unmatched 영역으로 이동 테스트")
    void moveImageToUnmatchedTest() {
        // given
        TripPlaceImageDto.ImageUnmatchedMoveReq imageReq =
                new TripPlaceImageDto.ImageUnmatchedMoveReq(fromPlaceId, imageId);

        Image image = buildImage();
        Place from = buildPlace(fromPlaceId, "목적지", List.of(image));
        TripDayPlace tripDayPlace = buildTripDayPlace(List.of(from));

        given(tripDayPlaceService.readById(tripDayPlaceId1)).willReturn(Optional.of(tripDayPlace));

        // when
        imageMovementService.moveImageToUnmatched(tripDayPlaceId1, imageReq);

        // then
        verify(tripDayPlaceService).deleteImage(tripDayPlaceId1, fromPlaceId, imageId);
        verify(tripDayPlaceService).saveImageToUnmatched(tripDayPlaceId1, image);
    }

    @Test
    @DisplayName("Unmatched 영역에서 Place로 이미지 이동 테스트")
    void MoveImageFromUnmatchedToPlaceTest() {
        // given
        TripPlaceImageDto.ImageUnmatchedMoveReq imageReq =
                new TripPlaceImageDto.ImageUnmatchedMoveReq(toPlaceId, imageId);

        Image image = buildImage();
        TripDayPlace tripDayPlace = buildTripDayPlace(List.of());
        tripDayPlace.addUnmatchedImages(List.of(image));

        given(tripDayPlaceService.readById(tripDayPlaceId1)).willReturn(Optional.of(tripDayPlace));

        // when
        imageMovementService.moveImageFromUnmatchedToPlace(tripDayPlaceId1, imageReq);

        // then
        verify(tripDayPlaceService).deleteImageFromUnMatched(tripDayPlaceId1, imageId);
        verify(tripDayPlaceService).saveImage(tripDayPlaceId1, toPlaceId, image);
    }
}
