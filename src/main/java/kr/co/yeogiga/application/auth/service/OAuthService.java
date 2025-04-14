package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.UserInfoDto;
import kr.co.yeogiga.application.auth.dto.UserStatusDto;
import kr.co.yeogiga.domain.oauth.entity.OAuth;
import kr.co.yeogiga.domain.oauth.service.OAuthDomainService;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.service.UserService;
import kr.co.yeogiga.domain.user.type.Role;
import kr.co.yeogiga.infrastructure.oauth.OAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final OAuthClientFactory oAuthClientFactory;
    private final OAuthDomainService oAuthDomainService;
    private final UserService userService;

    public void signIn(OAuthPlatform platform, String code) {
        OAuthClient oAuthClient = oAuthClientFactory.getOAuthClient(platform);

        String accessToken = oAuthClient.fetchAccessToken(code);
        UserInfoDto userInfo = oAuthClient.fetchUserInfo(accessToken);

        UserStatusDto userStatus = getUserStatus(platform, userInfo.platformId());

        // HACK: JWT 적용 시 로직 추가
    }

    private UserStatusDto getUserStatus(OAuthPlatform platform, String platformId) {
        return userService.readByPlatformAndPlatformId(platform, platformId)
                .map(user -> UserStatusDto.from(user, false))
                .orElseGet(() -> UserStatusDto.from(registerUser(platform, platformId), true));
    }

    private User registerUser(OAuthPlatform platform, String platformId) {
        User user = User.builder()
                .role(Role.USER)
                .nickname(platform + " " + platformId)
                .build();

        OAuth oauth = OAuth.builder()
                .platform(platform)
                .platformId(platformId)
                .user(user)
                .build();

        oAuthDomainService.save(oauth);

        return user;
    }
}