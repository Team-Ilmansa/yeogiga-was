package kr.co.yeogiga.presentation.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.application.tripplace.service.TripPlaceEditingService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;
import kr.co.yeogiga.infrastructure.config.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripPlaceEditingController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripPlaceEditingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripPlaceEditingService tripPlaceEditingService;

    private final Long tripId = 1L;
    private final int day = 1;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Nested
    @DisplayName("임시 저장소 목적지 테스트")
    class TempPlaceTest {

        private final TripPlaceReq.Request request = TripPlaceReq.Request.builder()
                .name("목적지1")
                .latitude(0.0)
                .longitude(0.0)
                .placeType("카페")
                .build();

        private final String placeId = "place-id";

        @Test
        @DisplayName("추가 성공")
        void addTempPlaceSuccess() throws Exception {
            // given
            doNothing().when(tripPlaceEditingService).addTempPlace(anyLong(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/temp-places", tripId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            resultActions.andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
        }

        @Test
        @DisplayName("조회 성공")
        void getTempPlacesSuccess() throws Exception {
            // given

            List<TripPlaceReq.StoredFormat> tempPlaces = List.of(
                    new TripPlaceReq.StoredFormat(placeId, "목적지1", 0.0, 0.0, "식당")
            );
            given(tripPlaceEditingService.getTempPlaces(tripId)).willReturn(tempPlaces);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/temp-places", tripId)
            );

            // then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].name").value("목적지1"))
                    .andExpect(jsonPath("$.data[0].placeCategory").value("식당"));
        }

        @Test
        @DisplayName("삭제 성공")
        void deleteTempPlaceSuccess() throws Exception {
            // given
            doNothing().when(tripPlaceEditingService).deleteTempPlace(anyLong(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/temp-places/{placeId}", tripId, placeId)
            );

            // then
            resultActions.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
        }
    }

    @Nested
    @DisplayName("목적지 추가 테스트")
    class AssignPlaceToDayTest {

        private final TripPlaceReq.Request request = TripPlaceReq.Request.builder()
                .name("목적지1")
                .latitude(0.0)
                .longitude(0.0)
                .placeType("카페")
                .build();

        private final String placeId = "place-id";

        @Test
        @DisplayName("성공")
        void addPlaceSuccess() throws Exception {
            // given
            doNothing().when(tripPlaceEditingService).assignPlaceToDay(anyLong(), anyInt(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/days/{day}/places/{placeId}", tripId, day, placeId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
        }

        @Test
        @DisplayName("실패 - 이미 존재하는 장소")
        void addPlaceFailAlreadyAdded() throws Exception {
            // given
            doThrow(new CustomException(TripErrorType.ALREADY_ADDED_PLACE))
                    .when(tripPlaceEditingService).assignPlaceToDay(anyLong(), anyInt(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/days/{day}/places/{placeId}", tripId, day, placeId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            );

            // then
            resultActions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(TripErrorType.ALREADY_ADDED_PLACE.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.ALREADY_ADDED_PLACE.getMessage()));
        }
    }

    @Test
    @DisplayName("목적지 수정 성공")
    void updatePlacesSuccess() throws Exception {
        // given
        List<TripPlaceReq.Request> requests = List.of(
                TripPlaceReq.Request.builder().name("목적지1").latitude(0.0).longitude(0.0).placeType("관광명소").build(),
                TripPlaceReq.Request.builder().name("목적지2").latitude(0.0).longitude(0.0).placeType("카페").build()
        );

        doNothing().when(tripPlaceEditingService).updatePlaces(anyLong(), anyInt(), Mockito.anyList());

        // when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/trip/{tripId}/days/{day}/places", tripId, day)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests))
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
        doNothing().when(tripPlaceEditingService).deleteAssignedPlace(anyLong(), anyInt(), Mockito.anyString());

        // when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/trip/{tripId}/days/{day}/places/{placeId}", tripId, day, "place-id")
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공하였습니다."));
    }

    @Test
    @DisplayName("목적지 조회 성공")
    void getAssignedPlacesSuccess() throws Exception {
        // given
        List<TripPlaceReq.StoredFormat> mockPlaces = List.of(
                new TripPlaceReq.StoredFormat("place-id", "목적지1", 33.123, 126.456, PlaceCategory.CAFE.getGroupName())
        );
        given(tripPlaceEditingService.getAssignedPlaces(tripId, day)).willReturn(mockPlaces);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/trip/{tripId}/days/{day}/places", tripId, day)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("목적지1"))
                .andExpect(jsonPath("$.data[0].placeCategory").value("식당"));
    }
}
