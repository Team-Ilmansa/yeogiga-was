package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.dto.UserInfoDto;
import kr.co.yeogiga.application.auth.dto.UserStatusDto;
import kr.co.yeogiga.common.jwt.JwtHelper;
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

@Service
@RequiredArgsConstructor
public class OAuthManagementService {
    private final OAuthClientFactory oAuthClientFactory;
    private final OAuthService oAuthService;
    private final JwtHelper jwtHelper;
    private final UserService userService;

    @Transactional
    public SignInDto.Response signIn(OAuthPlatform platform, String code) {
        OAuthClient oAuthClient = oAuthClientFactory.getOAuthClient(platform);

        String accessToken = oAuthClient.fetchAccessToken(code);
        UserInfoDto userInfo = oAuthClient.fetchUserInfo(accessToken);

        UserStatusDto userStatus = getUserStatus(platform, userInfo);

        return getSignInDto(userStatus);
    }

    private UserStatusDto getUserStatus(OAuthPlatform platform, UserInfoDto userInfo) {
        return userService.readByPlatformAndPlatformId(platform, userInfo.platformId())
                .map(user -> UserStatusDto.of(user, false))
                .orElseGet(() -> UserStatusDto.of(registerUser(platform, userInfo), true));
    }

    private User registerUser(OAuthPlatform platform, UserInfoDto userInfo) {
        User user = User.builder()
                .email(userInfo.email())
                .role(Role.USER)
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

    private SignInDto.Response getSignInDto(UserStatusDto userStatus) {
        if (!userStatus.shouldSignUp()) {
            User user = userStatus.user();
            String username = user.getUsername();
            String nickname = user.getNickname();
            Long userId = user.getId();

            String accessToken = jwtHelper.generateAccessToken(username, nickname, userId);
            String refreshToken = jwtHelper.generateRefreshToken(username, nickname, userId);

            return SignInDto.Response.builder()
                    .token(TokenDto.of(accessToken, refreshToken))
                    .shouldSignup(userStatus.shouldSignUp())
                    .build();
        }

        return SignInDto.Response.builder()
                .shouldSignup(userStatus.shouldSignUp())
                .build();
    }
}