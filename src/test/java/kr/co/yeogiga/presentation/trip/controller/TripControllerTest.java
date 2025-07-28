package kr.co.yeogiga.presentation.trip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.application.trip.dto.TripMemberRes;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.application.trip.dto.TripRes;
import kr.co.yeogiga.application.trip.service.TripCommandService;
import kr.co.yeogiga.application.trip.service.TripQueryService;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.tripplace.entity.Place;
import kr.co.yeogiga.domain.tripplace.type.PlaceCategory;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripCommandService tripCommandService;

    @MockBean
    private TripQueryService tripQueryService;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(put("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .alwaysDo(print())
                .build();

        User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("email")
                .role(Role.USER)
                .build();

        ReflectionTestUtils.setField(user, "id", 1L);

        userDetails = new CustomUserDetailsImpl(user);
    }

    @Nested
    @DisplayName("여행 생성")
    class TripCreation {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            TripReq.Creation creationRequest = TripReq.Creation.builder()
                    .title("test")
                    .build();

            when(tripCommandService.create(any(), any())).thenReturn(1L);

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsString(creationRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()))
                    .andExpect(jsonPath("$.data.tripId").value(1L));
        }

        @Test
        @DisplayName("유효성 검증 실패 - null")
        void failValidationNull() throws Exception {
            // given
            TripReq.Creation creationRequest = TripReq.Creation.builder()
//                    .title("test")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsString(creationRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        @DisplayName("유효성 검증 실패 - 최대 길이 초과")
        void failValidationLength() throws Exception {
            // given
            TripReq.Creation creationRequest = TripReq.Creation.builder()
                    .title("titletitletitletitletitle")
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip")
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsString(creationRequest))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists());
        }
    }

    @Nested
    @DisplayName("여행 시간 수정")
    class TimeModification {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 5, 2, 12, 00);
            TripReq.Time request = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            doNothing().when(tripCommandService).updateTime(any(), any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/time", 1L)
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증 실패")
        void failValidation() throws Exception {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 5, 2, 12, 00);
            TripReq.Time request = TripReq.Time.builder()
                    .start(startTime)
//                    .end(endTime)
                    .build();

            doNothing().when(tripCommandService).updateTime(any(), any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/time", 1L)
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.end").exists());
        }

        @Test
        @DisplayName("실패 - 이미 시작된 여행")
        void failAlreadyStart() throws Exception {
            // given
            LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 12, 00);
            LocalDateTime endTime = LocalDateTime.of(2025, 5, 2, 12, 00);
            TripReq.Time request = TripReq.Time.builder()
                    .start(startTime)
                    .end(endTime)
                    .build();

            doThrow(new CustomException(TripErrorType.TRIP_ALREADY_STARTED_OR_COMPLETED))
                    .when(tripCommandService).updateTime(any(), any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}/time", 1L)
                            .with(user(userDetails))
                            .content(objectMapper.writeValueAsBytes(request))
                            .contentType(MediaType.APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(TripErrorType.TRIP_ALREADY_STARTED_OR_COMPLETED.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.TRIP_ALREADY_STARTED_OR_COMPLETED.getMessage()));
        }
    }

    @Nested
    @DisplayName("메인 화면 내 여행 조회")
    class TripInMainView {

        private Place place = Place.builder()
                .id("place-id")
                .name("두류 공원")
                .latitude(11.11)
                .longitude(12.12)
                .order(10.0)
                .placeType(PlaceCategory.TOURISM)
                .build();

        private TripPlaceRes.PlaceSummary placeSummary = TripPlaceRes.PlaceSummary.from(place);

        @Test
        @DisplayName("계획 중인 여행")
        void successIfTripPlanned() throws Exception {
            // given
            TripRes.TripMainInfo tripMainInfo = TripRes.TripMainInfo.builder()
                    .tripId(1L)
                    .title("test")
                    .staredAt(LocalDateTime.of(2025, 5, 21, 12, 00))
                    .travelStatus(TravelStatus.PLANNED)
                    .places(List.of(placeSummary))
                    .build();

            when(tripQueryService.getTripMainInfo(any())).thenReturn(tripMainInfo);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/main")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.tripId").value(tripMainInfo.tripId()))
                    .andExpect(jsonPath("$.data.title").value(tripMainInfo.title()))
                    .andExpect(jsonPath("$.data.travelStatus").value(tripMainInfo.travelStatus().name()))
                    .andExpect(jsonPath("$.data.places").isArray());
        }

        @Test
        @DisplayName("진행 중인 여행")
        void successIfTripInProgress() throws Exception {
            // given
            TripRes.TripMainInfo tripMainInfo = TripRes.TripMainInfo.builder()
                    .tripId(1L)
                    .title("test")
                    .staredAt(LocalDateTime.of(2025, 5, 21, 12, 00))
                    .travelStatus(TravelStatus.IN_PROGRESS)
                    .places(List.of(placeSummary))
                    .build();

            when(tripQueryService.getTripMainInfo(any())).thenReturn(tripMainInfo);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/main")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.tripId").value(tripMainInfo.tripId()))
                    .andExpect(jsonPath("$.data.title").value(tripMainInfo.title()))
                    .andExpect(jsonPath("$.data.travelStatus").value(tripMainInfo.travelStatus().name()))
                    .andExpect(jsonPath("$.data.places").isArray());
        }

        @Test
        @DisplayName("예정이거나 진행 중인 여행이 없는 경우")
        void successIfNotTripPlannedAndInProgress() throws Exception {
            // given
            when(tripQueryService.getTripMainInfo(any())).thenReturn(null);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/main")
                            .with(user(userDetails))
              );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    @Nested
    @DisplayName("사용자가 속한 여행 조회")
    class GetAllTrip {
        private User user = User.builder()
                .username("username")
                .password("password")
                .email("test@test.com")
                .nickname("nickname")
                .role(Role.USER)
                .build();
        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            TripRes.TripSummary tripSummary = TripRes.TripSummary.builder()
                    .tripId(1L)
                    .title("title")
                    .startedAt(LocalDateTime.of(2025, 5, 19, 12, 0))
                    .endedAt(LocalDateTime.of(2025, 5, 20, 12, 0))
                    .status(TravelStatus.IN_PROGRESS)
                    .members(List.of(TripMemberRes.MemberInfo.fromEntity(user)))
                    .build();

            when(tripQueryService.getAllTrip(any())).thenReturn(List.of(tripSummary));

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].members").isArray());
        }
    }

    @Nested
    @DisplayName("여행 삭제")
    class TripDeletion {
        private final Long tripId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(tripCommandService).removeTrip(tripId);

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}", tripId)
                            .with(user(userDetails))
            );

            // then
            verify(tripCommandService, times(1)).removeTrip(tripId);

            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }
    }

    @Nested
    @DisplayName("여행 정보 업데이트")
    class TripInfoModification {
        private final Long tripId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            TripReq.Update updateRequest = TripReq.Update.builder()
                    .title("new title")
                    .build();

            doNothing().when(tripCommandService).updateTripInfo(tripId, updateRequest);

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateRequest))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 유효성 검증 실패: 제목 글자 수 초과")
        void failTitleLengthValidation() throws Exception {
            // given
            TripReq.Update updateRequest = TripReq.Update.builder()
                    .title("titletitletitletitletitle")
                    .build();

            doNothing().when(tripCommandService).updateTripInfo(tripId, updateRequest);

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateRequest))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        @DisplayName("실패 - 유효성 검증 실패: 빈 제목")
        void failTitleIsNull() throws Exception {
            // given
            TripReq.Update updateRequest = TripReq.Update.builder()
//                    .title("titletitletitletitletitle")
                    .build();

            doNothing().when(tripCommandService).updateTripInfo(tripId, updateRequest);

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateRequest))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        @DisplayName("실패 - 여행 미존재")
        void failIfTripNotFound() throws Exception {
            // given
            TripReq.Update updateRequest = TripReq.Update.builder()
                    .title("new title")
                    .build();

            doThrow(new CustomException(TripErrorType.TRIP_NOT_FOUND)).when(tripCommandService).updateTripInfo(tripId, updateRequest);

            // when
            ResultActions resultActions = mockMvc.perform(
                    put("/api/v1/trip/{tripId}", tripId)
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateRequest))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(TripErrorType.TRIP_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.TRIP_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("특정 여행 조회")
    class GetTrip {
        private final Long tripId = 1L;

        private Trip trip = Trip.builder()
                .title("title")
                .city("city")
                .leaderId(1L)
                .travelStatus(TravelStatus.IN_PROGRESS)
                .build();

        private User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("test@test.com")
                .role(Role.USER)
                .build();
        
        private TripMemberRes.MemberInfo member = TripMemberRes.MemberInfo.fromEntity(user);

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            TripRes.TripSummary tripSummary = TripRes.TripSummary.from(trip, List.of(member));
            when(tripQueryService.getTrip(tripId)).thenReturn(tripSummary);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value(trip.getTitle()))
                    .andExpect(jsonPath("$.data.members[0].nickname").value(user.getNickname()));
        }

        @Test
        @DisplayName("실패 - 여행 미존재")
        void failIfTripNotFound() throws Exception {
            // given
            doThrow(new CustomException(TripErrorType.TRIP_NOT_FOUND)).when(tripQueryService).getTrip(tripId);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(TripErrorType.TRIP_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.TRIP_NOT_FOUND.getMessage()));
        }
    }

    @Nested
    @DisplayName("준비 중 여행 조회")
    class GetSettingTrip {
        private TripRes.SettingTripInfo settingTripInfo = TripRes.SettingTripInfo.builder()
                .tripId(1L)
                .title("title")
                .status(TravelStatus.SETTING)
                .build();

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            when(tripQueryService.getSettingTrip(any())).thenReturn(List.of(settingTripInfo));

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/setting")
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].tripId").value(settingTripInfo.tripId()))
                    .andExpect(jsonPath("$.data[0].title").value(settingTripInfo.title()))
                    .andExpect(jsonPath("$.data[0].status").value(settingTripInfo.status().name()));
        }
    }
}
