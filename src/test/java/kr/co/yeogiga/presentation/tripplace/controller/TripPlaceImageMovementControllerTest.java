package kr.co.yeogiga.presentation.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDto;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageMovementService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.exception.ImageErrorType;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripPlaceImageMovementController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripPlaceImageMovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripPlaceImageMovementService tripPlaceImageMovementService;

    private final Long tripId = 1L;
    private final String tripDayPlaceId = "dayId";
    private final String fromPlaceId = "place1-id";
    private final String toPlaceId = "place2-id";
    private final String imageId = "image-id";

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Nested
    @DisplayName("같은 날짜 내 이미지 이동 테스트")
    class MoveImageToAnotherPlaceControllerTest {

        private final TripPlaceImageDto.ImageMoveReq req =
                new TripPlaceImageDto.ImageMoveReq(fromPlaceId, toPlaceId, imageId);

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(tripPlaceImageMovementService).moveImageToAnotherPlace(tripDayPlaceId, req);

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/trip/{tripId}/images/{tripDayPlaceId}/move", tripId, tripDayPlaceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
        }

        @Test
        @DisplayName("실패 - 여행 날짜(TripDayPlace) 없음")
        void FailNotFoundTripDayPlace() throws Exception {
            // given
            doThrow(new CustomException(TripErrorType.TRIP_PLACE_NOT_FOUND))
                    .when(tripPlaceImageMovementService).moveImageToAnotherPlace(tripDayPlaceId, req);

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/trip/{tripId}/images/{tripDayPlaceId}/move", tripId, tripDayPlaceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(TripErrorType.TRIP_PLACE_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.TRIP_PLACE_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("실패 - 출발지 목적지 없음")
        void failNotFoundPlace() throws Exception {
            // given
            doThrow(new CustomException(TripErrorType.PLACE_NOT_FOUND))
                    .when(tripPlaceImageMovementService).moveImageToAnotherPlace(tripDayPlaceId, req);

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/trip/{tripId}/images/{tripDayPlaceId}/move", tripId, tripDayPlaceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(TripErrorType.PLACE_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.PLACE_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("실패 - 이미지 없음")
        void failNotFoundImage() throws Exception {
            // given
            doThrow(new CustomException(ImageErrorType.NOT_FOUND))
                    .when(tripPlaceImageMovementService).moveImageToAnotherPlace(tripDayPlaceId, req);

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/trip/{tripId}/images/{tripDayPlaceId}/move", tripId, tripDayPlaceId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(ImageErrorType.NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(ImageErrorType.NOT_FOUND.getMessage()));
        }
    }

    @Test
    @DisplayName("다른 날짜 간 이미지 이동 성공")
    void moveImageBetweenDayPlacesSuccess() throws Exception {
        // given
        TripPlaceImageDto.ImageCrossDayMoveReq req =
                new TripPlaceImageDto.ImageCrossDayMoveReq("day1", fromPlaceId, "day2", toPlaceId, imageId);
        doNothing().when(tripPlaceImageMovementService).moveImageBetweenDayPlaces(req);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/images/move-between-days", tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("이미지를 Unmatched로 이동 성공")
    void moveImageToUnmatchedSuccess() throws Exception {
        // given
        TripPlaceImageDto.ImageUnmatchedMoveReq req = new TripPlaceImageDto.ImageUnmatchedMoveReq(fromPlaceId, imageId);
        doNothing().when(tripPlaceImageMovementService).moveImageToUnmatched(tripDayPlaceId, req);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/images/{tripDayPlaceId}/move-to-unmatched", tripId, tripDayPlaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("Unmatched에서 이미지 복원 성공")
    void moveImageFromUnmatchedToPlaceSuccess() throws Exception {
        // given
        TripPlaceImageDto.ImageUnmatchedMoveReq req = new TripPlaceImageDto.ImageUnmatchedMoveReq(toPlaceId, imageId);
        doNothing().when(tripPlaceImageMovementService).moveImageFromUnmatchedToPlace(tripDayPlaceId, req);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/images/{tripDayPlaceId}/move-from-unmatched", tripId, tripDayPlaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }
}
