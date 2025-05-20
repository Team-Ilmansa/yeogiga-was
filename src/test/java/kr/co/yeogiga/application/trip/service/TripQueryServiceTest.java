package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripRes;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TripQueryServiceTest {

    @Mock
    private TripMemberService tripMemberService;

    @InjectMocks
    private TripQueryService tripQueryService;

    @Nested
    @DisplayName("전체 여행 목록 조회")
    class GetAllTrip {
        private final Long userId = 1L;

        private Trip trip = Trip.builder()
                .title("title")
                .city("대구광역시")
                .leaderId(userId)
                .travelStatus(TravelStatus.PLANNED)
                .build();

        private User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .email("test@test.com")
                .role(Role.USER)
                .build();


        @Test
        @DisplayName("성공")
        void success() {
            // given
            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of(trip));

            // when
            List<TripRes.TripSummary> result = tripQueryService.getAllTrip(userId);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("성공 - 빈 배열 반환")
        void successEmptyList() {
            // given
            when(tripMemberService.readAllTripByUserId(userId)).thenReturn(List.of());

            // when
            List<TripRes.TripSummary> result = tripQueryService.getAllTrip(userId);

            // then
            assertThat(result).hasSize(0);
        }
    }


}
