package kr.co.yeogiga.presentation.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
import kr.co.yeogiga.application.trip.service.TripPlaceCommandService;
import kr.co.yeogiga.application.trip.service.TripPlaceSavingService;
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

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
        TripPlaceDto.CompleteRequest completeRequest = new TripPlaceDto.CompleteRequest(2);
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
        TripPlaceDto.InsertRequest insertRequest = TripPlaceDto.InsertRequest.builder()
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
    @DisplayName("목적지 정렬 성공")
    void reorderPlacesSuccess() throws Exception {
        // given
        TripPlaceDto.ReorderRequest reorderRequest = new TripPlaceDto.ReorderRequest(List.of("id1", "id2", "id3"));
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
        TripPlaceDto.ReorderRequest reorderRequest = new TripPlaceDto.ReorderRequest(List.of("id1", "id2"));
        doThrow(new CustomException(TripErrorType.DAY_PLACE_NOT_FOUND))
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
                .andExpect(jsonPath("$.code").value(TripErrorType.DAY_PLACE_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(TripErrorType.DAY_PLACE_NOT_FOUND.getMessage()));
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
