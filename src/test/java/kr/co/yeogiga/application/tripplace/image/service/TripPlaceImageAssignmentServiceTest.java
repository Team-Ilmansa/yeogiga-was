package kr.co.yeogiga.application.tripplace.image.service;

import kr.co.yeogiga.domain.tripplace.entity.Image;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.entity.TripDayPlace;
import kr.co.yeogiga.domain.tripplace.service.TripDayPlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TripPlaceImageAssignmentServiceTest {

    @Mock
    private TripDayPlaceService tripDayPlaceService;

    @InjectMocks
    private TripPlaceImageAssignmentService tripPlaceImageAssignmentService;

    private final String tripDayPlaceId = "tripDayPlaceId";

    private TripDayPlace tripDayPlace;
    private List<Place> places;
    private List<Image> tempImages;

    @BeforeEach
    void setup() {
        Place place = Place.builder()
                .id("place1")
                .name("Place1")
                .latitude(12.34)
                .longitude(56.78)
                .placeType("식당")
                .order(1.0)
                .build();

        places = new ArrayList<>();
        places.add(place);

        tripDayPlace = TripDayPlace.builder()
                .tripId(1L)
                .day(1)
                .places(places)
                .build();

        tempImages = new ArrayList<>();
    }

    @Test
    @DisplayName("이미지 매칭 성공 - GPS 정보 있는 이미지는 장소에, 없는 이미지는 unmatched에")
    void assignImageSuccessWithMixedImages() {
        // given
        Image imgWithGps = Image.builder()
                .url("img1")
                .latitude(37.001)
                .longitude(127.001)
                .date(LocalDateTime.now())
                .build();

        Image imgWithoutGps = Image.builder()
                .url("img2")
                .latitude(null)
                .longitude(null)
                .date(LocalDateTime.now())
                .build();

        tempImages.add(imgWithGps);
        tempImages.add(imgWithoutGps);

        // when
        tripPlaceImageAssignmentService.assignImageToTripDayPlace(tripDayPlace, tempImages);

        // then
        assertEquals(1, tripDayPlace.getPlaces().get(0).getImages().size());
        assertEquals(1, tripDayPlace.getUnmatchedImages().size());
        verify(tripDayPlaceService, times(1)).save(tripDayPlace);
    }
}

