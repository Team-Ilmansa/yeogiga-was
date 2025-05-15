package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageAssignmentService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageReassignmentService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripPlaceImageReassignmentServiceTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @Mock
    private TripPlaceImageAssignmentService tripPlaceImageAssignmentService;

    @InjectMocks
    private TripPlaceImageReassignmentService tripPlaceImageReassignmentService;

    private final String tripDayPlaceId = "day-id";
    @Test
    @DisplayName("할당된 이미지 목적지에 재정렬 성공")
    void reassignImagesToTripDayPlaceSuccess() {
        // given

        /* place1 */
        Image image1 = Image.builder().url("https://image1.com").latitude(1.1).longitude(2.2).build();
        Place place1 = Place.builder().name("목적지1").latitude(1.1).longitude(2.2).build();
        place1.addImages(List.of(image1));

        /* place2 */
        Image image2 = Image.builder().url("https://image2.com").latitude(3.3).longitude(4.4).build();
        Place place2 = Place.builder().name("목적지2").latitude(3.3).longitude(4.4).build();
        place2.addImages(List.of(image2));

        /* unmatched image */
        Image unmatched = Image.builder().url("https://image3.com").build();

        TripDayPlace tripDayPlace = TripDayPlace.builder()
                .places(List.of(place1, place2))
                .build();
        tripDayPlace.addUnmatchedImages(List.of(unmatched));

        given(tripDayPlaceService.readById(tripDayPlaceId)).willReturn(Optional.of(tripDayPlace));

        // when
        tripPlaceImageReassignmentService.reassignImagesToTripDayPlace(tripDayPlaceId);

        // then
        assertThat(place1.getImages()).isEmpty();
        assertThat(place2.getImages()).isEmpty();
        assertThat(tripDayPlace.getUnmatchedImages()).isEmpty();

        verify(tripPlaceImageAssignmentService).assignImageToTripDayPlace(eq(tripDayPlace), anyList());
    }

    @Test
    @DisplayName("할당된 이미지 목적지에 재정렬 실패 - 존재하지 않는 여행 일차")
    void reassignImagesToTripDayPlaceFailNotFound() {
        // given
        given(tripDayPlaceService.readById(tripDayPlaceId)).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                tripPlaceImageReassignmentService.reassignImagesToTripDayPlace(tripDayPlaceId)
        );

        // then
        assertEquals(TripErrorType.TRIP_PLACE_NOT_FOUND, exception.getErrorType());
    }
}
