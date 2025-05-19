package kr.co.yeogiga.presentation.tripplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.tripplace.dto.TripDaySummaryRes;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.application.tripplace.dto.VisitedMarkReq;
import kr.co.yeogiga.application.tripplace.service.TripPlaceCommandService;
import kr.co.yeogiga.application.tripplace.service.TripPlaceQueryService;
import kr.co.yeogiga.application.tripplace.service.TripPlaceSavingService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripPlaceController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripPlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripPlaceSavingService tripPlaceSavingService;

    @MockBean
    private TripPlaceCommandService tripPlaceCommandService;

    @MockBean
    private TripPlaceQueryService tripPlaceQueryService;

    private final Long tripId = 1L;
    private final String tripDayPlaceId = "dayId";
    private final String placeId = "placeId";

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @DisplayName("여행 목적지 확정 완료")
    void completeTripSuccess() throws Exception {
        // given
        TripPlaceReq.CompleteRequest completeRequest = new TripPlaceReq.CompleteRequest(2);
        doNothing().when(tripPlaceSavingService).completeTrip(tripId, completeRequest.lastDay());

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/trip/{tripId}/complete", tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(completeRequest))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("목적지 추가 성공")
    void addNewPlaceSuccess() throws Exception {
        // given
        TripPlaceReq.InsertRequest insertRequest = TripPlaceReq.InsertRequest.builder()
                .name("목적지1")
                .latitude(0.0)
                .longitude(0.0)
                .placeType("카페")
                .prevPlaceId("prevId")
                .nextPlaceId("nextId")
                .build();
        doNothing().when(tripPlaceCommandService).addNewPlace(tripDayPlaceId, insertRequest);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/places", tripId, tripDayPlaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insertRequest))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("여행 일차별 목적지 요약 정보 조회 성공")
    void getTripDayPlacesInfoSuccess() throws Exception {
        // given
        TripPlaceRes.TripDayPlaceInfo info = new TripPlaceRes.TripDayPlaceInfo("day1", 1, List.of());
        given(tripPlaceQueryService.getTripDayPlacesInfo(tripId)).willReturn(List.of(info));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/trip/{tripId}/day-place/places", tripId)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("여행 일차 목적지 상세 정보 조회 성공")
    void getPlaceDetailsInfoSuccess() throws Exception {
        // given
        TripPlaceRes.PlaceDetails details =
                new TripPlaceRes.PlaceDetails("place1", "목적지1", 0.0, 0.0, "카페", true);
        given(tripPlaceQueryService.getPlaceDetailsInfo(tripDayPlaceId)).willReturn(List.of(details));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/places", tripId, tripDayPlaceId)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("여행 ID로 여행 일차 요약 목록 조회에 성공한다")
    void getTripDaySummaries_success() throws Exception {
        // given
        Long tripId = 1L;
        TripDaySummaryRes.DayDto responseDto = new TripDaySummaryRes.DayDto(
                "day-id",
                1,
                List.of(new TripDaySummaryRes.PlaceDto("place-id", "목적지", 0.0, 1.0, "음식", null)),
                new TripDaySummaryRes.ImageDto("image-id", "url", 1.0, 2.0, LocalDateTime.now())
        );

        given(tripPlaceQueryService.getTripDaySummaries(tripId)).willReturn(List.of(responseDto));

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/trip/{tripId}/day-place", tripId)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value("day-id"))
                .andExpect(jsonPath("$.data[0].day").value(1))
                .andExpect(jsonPath("$.data[0].places[0].id").value("place-id"))
                .andExpect(jsonPath("$.data[0].unmatchedImage.id").value("image-id"));
    }


    @Test
    @DisplayName("목적지 정렬 성공")
    void reorderPlacesSuccess() throws Exception {
        // given
        TripPlaceReq.ReorderRequest reorderRequest = new TripPlaceReq.ReorderRequest(List.of("id1", "id2", "id3"));
        doNothing().when(tripPlaceCommandService).reorderPlaces(tripDayPlaceId, reorderRequest);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/places/order", tripId, tripDayPlaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderRequest))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("목적지 정렬 실패 - 일차가 존재하지 않음")
    void reorderPlacesFailDayPlaceNotFound() throws Exception {
        // given
        TripPlaceReq.ReorderRequest reorderRequest = new TripPlaceReq.ReorderRequest(List.of("id1", "id2"));
        doThrow(new CustomException(TripErrorType.TRIP_PLACE_NOT_FOUND))
                .when(tripPlaceCommandService).reorderPlaces(tripDayPlaceId, reorderRequest);

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/places/order", tripId, tripDayPlaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderRequest))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(TripErrorType.TRIP_PLACE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(TripErrorType.TRIP_PLACE_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("목적지 방문 여부 수정 성공")
    void markPlaceAsVisitedSuccess() throws Exception {
        // given
        VisitedMarkReq request = new VisitedMarkReq(true);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}/mark", tripId, tripDayPlaceId, placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("목적지 삭제 성공")
    void deletePlaceSuccess() throws Exception {
        // given
        doNothing().when(tripPlaceCommandService).deletePlace(tripDayPlaceId, placeId);

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/trip/{tripId}/day-place/{tripDayPlaceId}/places/{placeId}", tripId, tripDayPlaceId, placeId)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }
}
