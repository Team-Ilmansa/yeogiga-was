package kr.co.yeogiga.presentation.tripplace.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.tripplace.image.dto.ImageFavoriteReq;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageReq;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageRes;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageCommandService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageMovementService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageQueryService;
import kr.co.yeogiga.application.tripplace.image.service.TripPlaceImageReassignmentService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripPlaceImageController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripPlaceImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripPlaceImageMovementService tripPlaceImageMovementService;

    @MockBean
    private TripPlaceImageCommandService tripPlaceImageCommandService;

    @MockBean
    private TripPlaceImageQueryService tripPlaceImageQueryService;

    @MockBean
    private TripPlaceImageReassignmentService tripPlaceImageReassignmentService;

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
    @DisplayName("이미지 조회 테스트")
    class GetImageInfoTest {

        @Test
        @DisplayName("특정 장소의 이미지 조회")
        void getPlaceInfoSuccess() throws Exception {
            // given
            TripPlaceImageRes.PlaceImageInfo placeImageInfo = TripPlaceImageRes.PlaceImageInfo.builder()
                    .id("place-id")
                    .name("카페")
                    .latitude(1.1)
                    .longitude(2.2)
                    .placeType("식당")
                    .images(List.of())
                    .build();

            when(tripPlaceImageQueryService.getPlaceImageInfo(tripDayPlaceId, fromPlaceId))
                    .thenReturn(placeImageInfo);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}/images", tripId, tripDayPlaceId, fromPlaceId)
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."))
                    .andExpect(jsonPath("$.data.id").value("place-id"))
                    .andExpect(jsonPath("$.data.name").value("카페"));
        }

        @Test
        @DisplayName("Unmatched 이미지 조회")
        void getUnmatchedImageInfoSuccess() throws Exception {
            // given
            TripPlaceImageRes.UnmatchedImageInfo unmatchedImageInfo = TripPlaceImageRes.UnmatchedImageInfo.builder()
                    .images(List.of(
                            TripPlaceImageRes.ImageDto.builder()
                                    .id("image-id")
                                    .url("https://image.com")
                                    .latitude(0.0)
                                    .longitude(1.1)
                                    .date(LocalDateTime.now())
                                    .build()
                    ))
                    .build();

            when(tripPlaceImageQueryService.getUnmatchedImageInfo(tripDayPlaceId))
                    .thenReturn(unmatchedImageInfo);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/unmatched-images", tripId, tripDayPlaceId)
                            .accept(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."))
                    .andExpect(jsonPath("$.data.images").isArray());
        }
    }

    @Nested
    @DisplayName("같은 날짜 내 이미지 이동 테스트")
    class MoveImageToAnotherPlaceControllerTest {

        private final TripPlaceImageReq.ImageMove req =
                new TripPlaceImageReq.ImageMove(fromPlaceId, toPlaceId, imageId);

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(tripPlaceImageMovementService).moveImageToAnotherPlace(tripDayPlaceId, req);

            // when
            ResultActions resultActions = mockMvc.perform(
                    patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/move", tripId, tripDayPlaceId)
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
                    patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/move", tripId, tripDayPlaceId)
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
                    patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/move", tripId, tripDayPlaceId)
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
                    patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/move", tripId, tripDayPlaceId)
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
        TripPlaceImageReq.ImageCrossDayMove req =
                new TripPlaceImageReq.ImageCrossDayMove("day1", fromPlaceId, "day2", toPlaceId, imageId);
        doNothing().when(tripPlaceImageMovementService).moveImageBetweenDayPlaces(req);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/images/move", tripId)
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
        TripPlaceImageReq.ImageUnmatchedMove req = new TripPlaceImageReq.ImageUnmatchedMove(fromPlaceId, imageId);
        doNothing().when(tripPlaceImageMovementService).moveImageToUnmatched(tripDayPlaceId, req);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/unmatch", tripId, tripDayPlaceId)
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
        TripPlaceImageReq.ImageUnmatchedMove req = new TripPlaceImageReq.ImageUnmatchedMove(toPlaceId, imageId);
        doNothing().when(tripPlaceImageMovementService).moveImageFromUnmatchedToPlace(tripDayPlaceId, req);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/rematch", tripId, tripDayPlaceId)
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
    @DisplayName("이미지 목적지에 맞게 재정렬 테스트")
    void reassignImagesTest() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/re-assign", tripId, tripDayPlaceId)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("이미지 즐겨찾기 테스트")
    void updateImageFavoriteStatusTest() throws Exception {
        // given
        String placeId = "place-id";
        ImageFavoriteReq request = new ImageFavoriteReq(placeId, true);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/{imageId}/favorite", tripId, tripDayPlaceId, imageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Nested
    @DisplayName("이미지 삭제 테스트")
    class DeleteImageTest {

        private final String placeId = "place-id";
        private final String url1 = "https://image1.com";
        private final String url2 = "https://image2.com";

        @Test
        @DisplayName("단일 이미지 삭제 성공")
        void deleteSingleImageSuccess() throws Exception {
            // given
            TripPlaceImageDeleteDto.SingleDeleteReq deleteReq = new TripPlaceImageDeleteDto.SingleDeleteReq(
                    url1, TripPlaceImageDeleteDto.DeleteType.PLACE, placeId
            );

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/images/{imageId}", tripId, tripDayPlaceId, imageId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deleteReq))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
        }


        @Test
        @DisplayName("다중 이미지 삭제 성공")
        void deleteMultipleImagesSuccess() throws Exception {
            // given
            TripPlaceImageDeleteDto.MultiDeleteReq deleteReq = new TripPlaceImageDeleteDto.MultiDeleteReq(
                    List.of(url1, url2), List.of("image1-id", "image2-id")
            );

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/images", tripId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deleteReq))
            );
            resultActions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
        }
    }
}
