package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TripMemberCommandServiceTest {

    @Mock
    private TripMemberService tripMemberService;

    @Mock
    private TripService tripService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TripMemberCommandService tripMemberCommandService;

    @Nested
    @DisplayName("여행 멤버 참가")
    class JoinTrip {
        private final Long userId = 1L;
        private final Long tripId = 1L;

        private final Trip trip = Trip.builder()
                .title("title")
                .city("대구광역시")
                .leaderId(userId)
                .travelStatus(TravelStatus.IN_PROGRESS)
                .build();

        private final User user = User.builder()
                .username("username")
                .password("password")
                .email("test@test.com")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));
            when(userService.readById(userId)).thenReturn(Optional.of(user));
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(false);

            // when
            tripMemberCommandService.joinTrip(tripId, userId);

            // then
            verify(tripMemberService, times(1)).save(isA(TripMember.class));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 여행")
        void failNotFoundTrip() {
            // given
            when(tripService.readById(tripId)).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> tripMemberCommandService.joinTrip(tripId, userId));

            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void failNotFoundUser() {
            // given
            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));
            when(userService.readById(userId)).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> tripMemberCommandService.joinTrip(tripId, userId));

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 이미 여행에 참가 중인 사용자")
        void failAlreadyExists() {
            // given
            when(tripService.readById(tripId)).thenReturn(Optional.of(trip));
            when(userService.readById(userId)).thenReturn(Optional.of(user));
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);

            // when
            CustomException exception = assertThrows(CustomException.class, ()
                    -> tripMemberCommandService.joinTrip(tripId, userId));

            // then
            assertEquals(TripMemberErrorType.ALREADY_EXISTS, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("여행 탈퇴")
    class LeaveTrip {
        private final Long tripId = 1L;

        private final Long userId1 = 1L;
        private final Long userId2 = 2L;

        @Test
        @DisplayName("2명 이상의 멤버가 존재하고, 리더가 아닌 경우")
        void successIfMoreThan2MembersAndNotLeader() {
            // given
            when(tripMemberService.readAllUserIdByTripId(tripId)).thenReturn(List.of(userId1, userId2));
            when(tripService.readLeaderIdByTripId(tripId)).thenReturn(Optional.of(userId1));
            doNothing().when(tripMemberService).deleteByTripIdAndUserId(tripId, userId2);

            // when
            tripMemberCommandService.leaveTrip(tripId, userId2);

            // then
            verify(tripService, times(0)).deleteById(tripId);
        }

        @Test
        @DisplayName("여행 멤버가 1명인 경우(방장 본인만 남은 상태)")
        void successIfOnlyOneMember() {
            // given
            when(tripMemberService.readAllUserIdByTripId(tripId)).thenReturn(List.of(userId1));
            when(tripService.readLeaderIdByTripId(tripId)).thenReturn(Optional.of(userId1));
            doNothing().when(tripMemberService).deleteByTripIdAndUserId(tripId, userId1);

            // when
            tripMemberCommandService.leaveTrip(tripId, userId1);

            // then
            verify(tripService, times(1)).deleteById(tripId);
        }

        @Test
        @DisplayName("실패 - 멤버가 아닌 경우")
        void failIfNotMember() {
            // given
            when(tripMemberService.readAllUserIdByTripId(tripId)).thenReturn(List.of(userId1));

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripMemberCommandService.leaveTrip(tripId, userId2));

            // then
            assertEquals(TripMemberErrorType.IS_NOT_MEMBER, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 여행 리더이고, 멤버가 2명 이상인 경우")
        void failIfLeaderAndMemberIsMoreThan2() {
            // given
            when(tripMemberService.readAllUserIdByTripId(tripId)).thenReturn(List.of(userId1, userId2));
            when(tripService.readLeaderIdByTripId(tripId)).thenReturn(Optional.of(userId1));

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripMemberCommandService.leaveTrip(tripId, userId1));

            // then
            assertEquals(TripMemberErrorType.LEADER_CAN_NOT_LEAVE_TRIP, exception.getErrorType());
        }
    }

    @Nested
    @DisplayName("여행 멤버 추방")
    class KickMember {
        private final Long tripId = 1L;
        private final Long leaderId = 1L;
        private final Long targetUserId = 2L;

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripService.readLeaderIdByTripId(tripId)).thenReturn(Optional.of(leaderId));
            doNothing().when(tripMemberService).deleteByTripIdAndUserId(tripId, targetUserId);

            // when
            tripMemberCommandService.kickMember(tripId, leaderId, targetUserId);

            // then
            verify(tripMemberService, times(1)).deleteByTripIdAndUserId(tripId, targetUserId);
        }

        @Test
        @DisplayName("실패 - 방장이 아닌 사용자가 요청한 경우")
        void failIfNotLeader() {
            // given
            when(tripService.readLeaderIdByTripId(tripId)).thenReturn(Optional.of(3L));

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripMemberCommandService.kickMember(tripId, leaderId, targetUserId));

            // then
            assertEquals(TripMemberErrorType.ONLY_LEADER, exception.getErrorType());
        }

        @Test
        @DisplayName("실패 - 자기 자신을 추방하려는 경우")
        void failIfSelfKick() {
            // given
            when(tripService.readLeaderIdByTripId(tripId)).thenReturn(Optional.of(leaderId));

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripMemberCommandService.kickMember(tripId, leaderId, leaderId));

            // then
            assertEquals(TripMemberErrorType.CAN_NOT_SELF_KICK, exception.getErrorType());
        }
    }
}
