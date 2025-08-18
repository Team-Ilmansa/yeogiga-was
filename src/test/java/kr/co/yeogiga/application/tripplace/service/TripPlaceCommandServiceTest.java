package kr.co.yeogiga.application.tripplace.service;

import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Place;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripDay;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.PlaceService;
import kr.co.yeogiga.domain.trip.service.TripDayService;
import kr.co.yeogiga.domain.trip.type.PlaceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TripPlaceCommandServiceTest {

    @Mock
    private TripDayService tripDayService;

    @Mock
    private PlaceService placeService;

    @InjectMocks
    private TripPlaceCommandService tripPlaceCommandService;

    private final Long tripId = 1L;
    private final Long tripDayId = 10L;


    @Nested
    @DisplayName("새로운 목적지 추가 테스트")
    class AddNewsPlaceTest {

        private final int day = 1;
        private TripDay tripDay;
        private TripPlaceReq.Request request;

        @Captor
        private ArgumentCaptor<Place> placeCaptor;

        @BeforeEach
        void setUp() {
            tripDay = TripDay.builder()
                    .trip(mock(Trip.class))
                    .day(day)
                    .build();

            ReflectionTestUtils.setField(tripDay, "id", tripDayId);

            request = TripPlaceReq.Request.builder()
                    .name("목적지1")
                    .latitude(0.0)
                    .longitude(0.0)
                    .placeType(PlaceCategory.RESTAURANT)
                    .build();
        }

        @Test
        @DisplayName("실패 - 여행 일정이 없는 경우")
        void failWhenTripDayNotFound() {
            // given
            given(tripDayService.readByTripIdAndDay(tripId, day)).willReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripPlaceCommandService.addNewPlace(tripId, day, request));

            // then
            assertEquals(TripErrorType.TRIP_DAY_NOT_FOUND, exception.getErrorType());
            verify(placeService, never()).save(any(Place.class));
        }

        @Test
        @DisplayName("가장 처음 추가되는 경우 - 기존에 목적지 없는 상태")
        void addPlaceFirst() {
            // given
            given(tripDayService.readByTripIdAndDay(tripId, day)).willReturn(Optional.ofNullable(tripDay));
            given(placeService.countByTripDayId(tripDayId)).willReturn(0);

            // when
            tripPlaceCommandService.addNewPlace(tripId, day, request);

            // then
            verify(placeService, times(1)).save(placeCaptor.capture());

            Place captured = placeCaptor.getValue();
            assertEquals(1, captured.getSortOrder());
        }

        @Test
        @DisplayName("가장 뒤에 추가되는 경우")
        void addPlaceBack() {
            // given
            given(tripDayService.readByTripIdAndDay(tripId, day)).willReturn(Optional.ofNullable(tripDay));
            given(placeService.countByTripDayId(tripDayId)).willReturn(3);

            // when
            tripPlaceCommandService.addNewPlace(tripId, day, request);

            // then
            verify(placeService, times(1)).save(placeCaptor.capture());

            Place captured = placeCaptor.getValue();
            assertEquals(4, captured.getSortOrder());
        }

    }
}
