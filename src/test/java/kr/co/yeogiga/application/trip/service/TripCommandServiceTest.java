package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.domain.trip.entity.Trip;
import kr.co.yeogiga.domain.trip.entity.TripMember;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.trip.service.TripService;
import kr.co.yeogiga.domain.trip.type.TravelStatus;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripCommandServiceTest {
    @Mock
    private TripService tripService;

    @Mock
    private TripMemberService tripMemberService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TripCommandService tripCommandService;

    @Nested
    @DisplayName("여행 생성")
    class TripCreation {
        private final Long leaderId = 1L;

        private TripReq.Creation creationRequest = TripReq.Creation.builder()
                .title("test")
                .city("대구광역시")
                .build();

        @Captor
        private ArgumentCaptor<Trip> tripCaptor;

        @Test
        @DisplayName("성공")
        void success() {
            // given
            User user = User.builder()
                    .username("username")
                    .password("password")
                    .nickname("nickname")
                    .email("email")
                    .role(Role.USER)
                    .build();

            when(userService.readById(any())).thenReturn(Optional.of(user));
            doNothing().when(tripService).save(any());
            doNothing().when(tripMemberService).save(any());


            // when
            tripCommandService.create(leaderId, creationRequest);

            // then
            verify(tripService).save(tripCaptor.capture());

            Trip capturedTrip = tripCaptor.getValue();
            assertEquals(creationRequest.title(), capturedTrip.getTitle());
            assertEquals(creationRequest.city(), capturedTrip.getCity());
            assertEquals(leaderId, capturedTrip.getLeaderId());
            assertEquals(TravelStatus.PLANNED, capturedTrip.getTravelStatus());
        }
    }
}
