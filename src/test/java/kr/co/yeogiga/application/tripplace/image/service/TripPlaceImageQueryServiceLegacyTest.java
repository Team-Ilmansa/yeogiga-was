package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.application.tripplace.image.dto.FavoriteImageRes;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.placeimage.entity.Image;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripPlaceImageQueryServiceLegacyTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @InjectMocks
    private TripPlaceImageQueryServiceLegacy tripPlaceImageQueryServiceLegacy;

    private final String tripDayPlaceId = "tripDayPlace-id";
    private final String placeId = "place-id";

    private final Image image = Image.builder()
            .url("https://image.com")
            .latitude(0.0)
            .longitude(1.1)
            .date(LocalDateTime.of(2024, 5, 14, 12, 0))
            .build();

    private final Place place = Place.builder()
            .id(placeId)
            .name("카페")
            .latitude(1.1)
            .longitude(2.2)
            .placeType(PlaceCategory.RESTAURANT)
            .order(10.0)
            .build();

    @Test
    @DisplayName("여행 일차에 대한 이미지 조회 테스트")
    void getTripDayImageInfoTest() {
        // given
        List<Image> images = List.of(image);
        Long tripId = 1L;
        int day = 1;

        when(tripDayPlaceService.readAllImagesByTripIdAndDay(tripId, day))
                .thenReturn(images);

        // when
        List<TripPlaceImageRes.ImageDto> result =
                tripPlaceImageQueryServiceLegacy.getTripDayImageInfo(tripId, day);

        // then
        assertThat(result).hasSize(1);
    }

    @Nested
    @DisplayName("특정 여행 일차 및 목적지의 이미지 목록 조회 테스트")
    class PlaceImageInfo {

        @Test
        @DisplayName("성공")
        void getPlaceImageInfoSuccess() {
            // given
            place.addImages(List.of(image));

            when(tripDayPlaceService.readPlaceByIdAndPlaceId(tripDayPlaceId, placeId))
                    .thenReturn(Optional.of(place));

            // when
            TripPlaceImageRes.PlaceImageInfo result = tripPlaceImageQueryServiceLegacy.getPlaceImageInfo(tripDayPlaceId, placeId);

            // then
            assertEquals(placeId, result.id());
            assertThat(result.images()).hasSize(1);
        }

        @Test
        @DisplayName("실패 - 목적지 존재하지 않음")
        void getPlaceImageInfoNotFound() {
            // given
            when(tripDayPlaceService.readPlaceByIdAndPlaceId(tripDayPlaceId, placeId))
                    .thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripPlaceImageQueryServiceLegacy.getPlaceImageInfo(tripDayPlaceId, placeId));

            // then
            assertEquals(TripErrorType.PLACE_NOT_FOUND, exception.getErrorType());
        }
    }

    @Test
    @DisplayName("특정 여행 일차의 unmatched 이미지 목록 조회 테스트")
    void getUnmatchedImageInfoTest() {
        // given
        when(tripDayPlaceService.readUnmatchedImagesById(tripDayPlaceId))
                .thenReturn(List.of(image));

        // when
        TripPlaceImageRes.UnmatchedImageInfo result = tripPlaceImageQueryServiceLegacy.getUnmatchedImageInfo(tripDayPlaceId);

        // then
        assertEquals(image.getUrl(), result.images().get(0).url());
        assertThat(result.images()).hasSize(1);
    }

    @Test
    @DisplayName("특정 여행에서 즐겨찾기한 이미지 조회 테스트")
    void getFavoriteImagesTest() {
        // given
        given(tripDayPlaceService.readFavoriteImages(tripDayPlaceId))
                .willReturn(List.of(image));

        // when
        List<FavoriteImageRes> result = tripPlaceImageQueryServiceLegacy.getFavoriteImages(tripDayPlaceId);

        // then
        assertEquals(1, result.size());
    }
}
