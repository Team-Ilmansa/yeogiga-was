package kr.co.yeogiga.application.trip.service;

import kr.co.yeogiga.application.trip.dto.TripMemberLocationDto;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.trip.exception.TripMemberErrorType;
import kr.co.yeogiga.domain.trip.service.TripMemberService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import kr.co.yeogiga.infrastructure.redis.RedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TripMemberLocationQueryServiceTest {

    @Mock
    private RedisRepository redisRepository;
    
    @Mock
    private TripMemberService tripMemberService;
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private TripMemberLocationQueryService tripMemberLocationQueryService;
    
    @Nested
    @DisplayName("여행 멤버 위치 조회")
    class GetMemberLocations {
        private final Long tripId = 1L;
        private final Long userId = 1L;
        
        @Test
        @DisplayName("성공")
        void success() {
            // given
            TripMemberLocationDto.StoredFormat storedInfo
                    = new TripMemberLocationDto.StoredFormat(1.1, 2.2, 1L);
            
            User user = User.builder()
                    .username("username")
                    .password("password")
                    .nickname("nickname")
                    .role(Role.USER)
                    .email("test@test.com")
                    .build();
            
            ReflectionTestUtils.setField(user, "id", userId);
            
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            when(redisRepository.getHashAll(anyString(), any(), any())).thenReturn(List.of(storedInfo));
            when(userService.readAllByIds(any())).thenReturn(List.of(user));
            
            // when
            List<TripMemberLocationDto.Response> result
                    = tripMemberLocationQueryService.readMemberLocations(tripId, userId);
            
            // then
            assertThat(result).hasSize(1);
            assertEquals(1.1, result.get(0).latitude());
            assertEquals(2.2, result.get(0).longitude());
            assertEquals(userId, result.get(0).userId());
            assertEquals(user.getNickname(), result.get(0).nickname());
            assertEquals(user.getImageUrl(), result.get(0).imageUrl());
        }
        
        @Test
        @DisplayName("성공 - 아직 저장된 위치 정보가 없을 경우")
        void successIfNoStoredInfo() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(true);
            when(redisRepository.getHashAll(anyString(), any(), any())).thenReturn(Collections.emptyList());
            
            // when
            List<TripMemberLocationDto.Response> result
                    = tripMemberLocationQueryService.readMemberLocations(tripId, userId);
            
            // then
            assertThat(result).hasSize(0);
        }
        
        @Test
        @DisplayName("실패 - 여행이 존재하기 않거나 멤버가 아닌 경우")
        void failIfTripNotFoundOrNotMember() {
            // given
            when(tripMemberService.existsByTripIdAndUserId(tripId, userId)).thenReturn(false);
            
            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> tripMemberLocationQueryService.readMemberLocations(tripId, userId));
            
            // then
            assertEquals(TripMemberErrorType.IS_NOT_MEMBER, exception.getErrorType());
        }
    }
    
}
