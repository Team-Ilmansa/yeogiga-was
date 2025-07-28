package kr.co.yeogiga.presentation.trip.controller;

import kr.co.yeogiga.application.trip.dto.TripMemberRes;
import kr.co.yeogiga.application.trip.service.TripMemberCommandService;
import kr.co.yeogiga.application.trip.service.TripMemberQueryService;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.common.security.filter.JwtAuthenticationFilter;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TripMemberController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
        }
)
public class TripMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripMemberCommandService tripMemberCommandService;

    @MockBean
    private TripMemberQueryService tripMemberQueryService;

    private CustomUserDetailsImpl userDetails;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(patch("/**").with(csrf()))
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
    @DisplayName("여행 참가")
    class JoinTrip {
        private final Long tripId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(tripMemberCommandService).joinTrip(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.created().message()));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 여행")
        void failNotFoundTrip() throws Exception {
            // given
            doThrow(new CustomException(TripErrorType.TRIP_NOT_FOUND)).when(tripMemberCommandService).joinTrip(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(TripErrorType.TRIP_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(TripErrorType.TRIP_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void failNotFoundUser() throws Exception {
            // given
            doThrow(new CustomException(UserErrorType.NOT_FOUND)).when(tripMemberCommandService).joinTrip(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(UserErrorType.NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(UserErrorType.NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("실패 - 이미 여행에 참가 중인 사용자")
        void failAlreadyExists() throws Exception {
            // given
            doThrow(new CustomException(TripMemberErrorType.ALREADY_EXISTS)).when(tripMemberCommandService).joinTrip(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(TripMemberErrorType.ALREADY_EXISTS.getCode()))
                    .andExpect(jsonPath("$.message").value(TripMemberErrorType.ALREADY_EXISTS.getMessage()));
        }
    }

    @Nested
    @DisplayName("여행 멤버 조회")
    class TripMemberInfo {
        private final Long tripId = 1L;

        private TripMemberRes.MemberInfo memberInfo1 = TripMemberRes.MemberInfo.builder()
                .userId(1L)
                .nickname("nick1")
                .imageUrl("http://image1.com")
                .build();

        private TripMemberRes.MemberInfo memberInfo2 = TripMemberRes.MemberInfo.builder()
                .userId(2L)
                .nickname("nick2")
                .build();

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            when(tripMemberQueryService.getTripMembers(tripId)).thenReturn(List.of(memberInfo1, memberInfo2));

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].userId").value(1L))
                    .andExpect(jsonPath("$.data[1].userId").value(2L));;
        }
    }

    @Nested
    @DisplayName("여행 멤버 탈퇴")
    class LeaveTrip {
        private final Long tripId = 1L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(tripMemberCommandService).leaveTrip(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 멤버가 2명 이상이고 요청자가 방장인 경우")
        void failIfMemberIsMoreThan2AndLeader() throws Exception {
            // given
            doThrow(new CustomException(TripMemberErrorType.LEADER_CAN_NOT_LEAVE_TRIP))
                    .when(tripMemberCommandService).leaveTrip(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(TripMemberErrorType.LEADER_CAN_NOT_LEAVE_TRIP.getCode()))
                    .andExpect(jsonPath("$.message").value(TripMemberErrorType.LEADER_CAN_NOT_LEAVE_TRIP.getMessage()));
        }

        @Test
        @DisplayName("실패 - 여행 멤버가 아닌 경우")
        void failItNotMember() throws Exception {
            // given
            doThrow(new CustomException(TripMemberErrorType.IS_NOT_MEMBER))
                    .when(tripMemberCommandService).leaveTrip(any(), any());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/members", tripId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(TripMemberErrorType.IS_NOT_MEMBER.getCode()))
                    .andExpect(jsonPath("$.message").value(TripMemberErrorType.IS_NOT_MEMBER.getMessage()));
        }
    }

    @Nested
    @DisplayName("여행 멤버 추방")
    class KickMember {
        private final Long tripId = 1L;
        private final Long targetUserId = 2L;

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            doNothing().when(tripMemberCommandService).kickMember(anyLong(), any(), anyLong());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/members/{memberId}", tripId, targetUserId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(SuccessResponse.ok().message()));
        }

        @Test
        @DisplayName("실패 - 방장이 아닌 사용자가 요청한 경우")
        void failIfNotLeader() throws Exception {
            // given
            doThrow(new CustomException(TripMemberErrorType.ONLY_LEADER)).when(tripMemberCommandService).kickMember(anyLong(), any(), anyLong());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/members/{memberId}", tripId, targetUserId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(TripMemberErrorType.ONLY_LEADER.getCode()))
                    .andExpect(jsonPath("$.message").value(TripMemberErrorType.ONLY_LEADER.getMessage()));
        }

        @Test
        @DisplayName("실패 - 자기 자신을 추방하려는 경우")
        void failIfSelfKick() throws Exception {
            // given
            doThrow(new CustomException(TripMemberErrorType.CAN_NOT_SELF_KICK)).when(tripMemberCommandService).kickMember(anyLong(), any(), anyLong());

            // when
            ResultActions resultActions = mockMvc.perform(
                    delete("/api/v1/trip/{tripId}/members/{memberId}", tripId, targetUserId)
                            .with(user(userDetails))
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(TripMemberErrorType.CAN_NOT_SELF_KICK.getCode()))
                    .andExpect(jsonPath("$.message").value(TripMemberErrorType.CAN_NOT_SELF_KICK.getMessage()));
        }
    }
}
