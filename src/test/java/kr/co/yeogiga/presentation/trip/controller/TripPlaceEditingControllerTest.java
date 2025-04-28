package kr.co.yeogiga.presentation.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
import kr.co.yeogiga.application.trip.service.TripPlaceEditingService;
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
    @DisplayName("목적지 추가 테스트")
    class AddPlaceTest {

        private final TripPlaceDto.Request request =
                new TripPlaceDto.Request("목적지1", 33.1234, 126.5678, "카페");

        @Test
        @DisplayName("성공")
        void addPlaceSuccess() throws Exception {
            // given
            doNothing().when(tripPlaceEditingService).addPlace(anyLong(), anyInt(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/days/{day}/places", tripId, day)
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
                    .when(tripPlaceEditingService).addPlace(anyLong(), anyInt(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/days/{day}/places", tripId, day)
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
        List<TripPlaceDto.Request> requests = List.of(
                new TripPlaceDto.Request("목적지1", 33.4567, 126.7890, "관광명소"),
                new TripPlaceDto.Request("목적지2", 33.1234, 126.5678, "카페")
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
        doNothing().when(tripPlaceEditingService).deletePlace(anyLong(), anyInt(), Mockito.anyString());

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
    void getPlacesSuccess() throws Exception {
        // given
        List<TripPlaceDto.StoredFormat> mockPlaces = List.of(
                new TripPlaceDto.StoredFormat("place-id", "목적지1", 33.123, 126.456, PlaceCategory.CAFE)
        );
        given(tripPlaceEditingService.getPlaces(tripId, day)).willReturn(mockPlaces);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/trip/{tripId}/days/{day}/places", tripId, day)
        );

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("목적지1"))
                .andExpect(jsonPath("$.data[0].placeCategory").value("CAFE"));
    }
}
