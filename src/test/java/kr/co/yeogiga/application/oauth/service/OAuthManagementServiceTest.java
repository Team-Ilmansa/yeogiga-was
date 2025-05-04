package kr.co.yeogiga.application.oauth.service;

import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.service.JwtService;
import kr.co.yeogiga.application.auth.service.OAuthClientFactory;
import kr.co.yeogiga.application.auth.service.OAuthManagementService;
import kr.co.yeogiga.domain.oauth.service.OAuthService;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OAuthManagementServiceTest {

    @Mock
    private OAuthClientFactory oAuthClientFactory;

    @Mock
    private OAuthService oAuthService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OAuthManagementService oAuthManagementService;

    @Nested
    @DisplayName("회원 등록 - 게스트 권한 승격")
    class Register {
        private User user = User.builder()
                .username("testid")
                .password("testpassword")
                .nickname("KAKAO 123")
                .email("test@test.com")
                .role(Role.GUEST)
                .build();

        private final Long userId = 1L;

        @Test
        @DisplayName("성공")
        void success() {
            // given
            String updatedNickname = "newNickname";
            SignUpDto.Register request = new SignUpDto.Register(updatedNickname);

            when(userService.readById(userId)).thenReturn(Optional.of(user));

            // when
            oAuthManagementService.register(userId, request);

            // then
            assertEquals(user.getNickname(), updatedNickname);
            assertEquals(user.getRole(), Role.USER);
            assertEquals(user.isSignedUp(), true);
        }
    }
}
