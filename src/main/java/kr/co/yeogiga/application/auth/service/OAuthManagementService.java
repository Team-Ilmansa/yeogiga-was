package kr.co.yeogiga.application.auth.service;

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

@Service
@RequiredArgsConstructor
public class OAuthManagementService {
    private final OAuthClientFactory oAuthClientFactory;
    private final OAuthService oAuthService;
    private final UserService userService;

    @Transactional
    public void signIn(OAuthPlatform platform, String code) {
        OAuthClient oAuthClient = oAuthClientFactory.getOAuthClient(platform);

        String accessToken = oAuthClient.fetchAccessToken(code);
        UserInfoDto userInfo = oAuthClient.fetchUserInfo(accessToken);

        UserStatusDto userStatus = getUserStatus(platform, userInfo);

        // HACK: JWT 적용 시 로직 추가
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
}