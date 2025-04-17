package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.dto.UserInfoDto;
import kr.co.yeogiga.application.auth.dto.UserStatusDto;
import kr.co.yeogiga.domain.oauth.entity.OAuth;
import kr.co.yeogiga.domain.oauth.service.OAuthService;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import kr.co.yeogiga.infrastructure.oauth.OAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuthManagementService {
    private final OAuthClientFactory oAuthClientFactory;
    private final OAuthService oAuthService;
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * OAuth2 로그인
     *
     * @param platform      OAuth 로그인 플랫폼
     * @param code          OAuth 리소스 서버 제공 인증 코드
     * @return              로그인 응답 정보 (JWT, 추가 정보 입력 필요 여부)
     */
    @Transactional
    public SignInDto.Response signIn(OAuthPlatform platform, String code) {
        OAuthClient oAuthClient = oAuthClientFactory.getOAuthClient(platform);

        String accessToken = oAuthClient.fetchAccessToken(code);
        UserInfoDto userInfo = oAuthClient.fetchUserInfo(accessToken);

        UserStatusDto userStatus = getUserStatus(platform, userInfo);

        return getSignInDto(userStatus);
    }

    /**
     * User 및 추가정보 입력 필요 여부 반환 메서드
     *
     * @param platform      OAuth 로그인 플랫폼
     * @param userInfo      OAuth 리소스 서버 제공 사용자 정보
     * @return              User, 추가 정보 입력 필요 여부
     */
    private UserStatusDto getUserStatus(OAuthPlatform platform, UserInfoDto userInfo) {
        return userService.readByPlatformAndPlatformId(platform, userInfo.platformId())
                .map(user -> Objects.isNull(user.getPassword())
                        ? UserStatusDto.of(user, true)
                        : UserStatusDto.of(user, false)
                )
                .orElseGet(() -> UserStatusDto.of(registerUser(platform, userInfo), true));
    }

    /**
     * User 및 OAuth 저장 메서드
     *
     * @param platform      OAuth 로그인 플랫폼
     * @param userInfo      OAuth 리소스 서버 제공 유저 사용자 정보
     * @return              User
     */
    private User registerUser(OAuthPlatform platform, UserInfoDto userInfo) {
        User user = User.builder()
                .email(userInfo.email())
                .role(Role.USER)
                .username(platform + " " + userInfo.platformId())
                .nickname(platform + " " + userInfo.platformId())
                .build();

        OAuth oauth = OAuth.builder()
                .platform(platform)
                .platformId(userInfo.platformId())
                .user(user)
                .build();

        oAuthService.save(oauth);

        return user;
    }

    /**
     * 로그인 요청 응답 DTO 생성 메서드
     *
     * @param userStatus    User, 추가 정보 입력 여부
     * @return              로그인 응답 정보 (JWT, 추가 정보 입력 필요 여부)
     */
    private SignInDto.Response getSignInDto(UserStatusDto userStatus) {
        if (!userStatus.shouldSignUp()) {
            User user = userStatus.user();
            String username = user.getUsername();
            String nickname = user.getNickname();
            Long userId = user.getId();

            TokenDto token = jwtService.generateToken(username, nickname, userId);

            return SignInDto.Response.builder()
                    .token(TokenDto.of(token.accessToken(), token.refreshToken()))
                    .shouldSignup(userStatus.shouldSignUp())
                    .build();
        }

        return SignInDto.Response.builder()
                .shouldSignup(userStatus.shouldSignUp())
                .build();
    }
}