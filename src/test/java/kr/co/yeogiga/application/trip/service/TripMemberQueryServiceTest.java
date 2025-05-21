package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripMemberRes;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripMemberQueryServiceTest {

    @Mock
    private TripMemberService tripMemberService;

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripMemberQueryService tripMemberQueryService;

    @Nested
    @DisplayName("여행 멤버 조회")
    class TripMember {
        private final Long tripId = 1L;

        private User user1 = User.builder()
                .username("user1")
                .password("password1")
                .email("test1@test.com")
                .nickname("nickname1")
                .role(Role.USER)
                .build();

        private User user2 = User.builder()
                .username("user2")
                .password("password2")
                .email("test2@test.com")
                .nickname("nickname2")
                .role(Role.USER)
                .build();

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(user1, "id", 1L);
            ReflectionTestUtils.setField(user2, "id", 2L);
        }

        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripService.existsById(tripId)).thenReturn(true);
            when(tripMemberService.readAllUserByTripId(tripId)).thenReturn(List.of(user1, user2));

            // when
            List<TripMemberRes.MemberInfo> result = tripMemberQueryService.getTripMembers(tripId);

            // then
            assertThat(result).hasSize(2);
            assertEquals(user1.getId(), result.get(0).userId());
            assertEquals(user2.getId(), result.get(1).userId());
        }

        @Test
        @DisplayName("실패 - 여행 조회 불가")
        void failIfTripNotFound() {
            // given
            when(tripService.existsById(tripId)).thenReturn(false);

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripMemberQueryService.getTripMembers(tripId));

            // then
            assertEquals(TripErrorType.TRIP_NOT_FOUND, exception.getErrorType());
        }
    }
}
