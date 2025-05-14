package kr.co.yeogiga.application.auth.service;

import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.dto.TokenDto;
import kr.co.yeogiga.application.auth.dto.UserInfoDto;
import kr.co.yeogiga.application.auth.dto.UserStatusDto;
import kr.co.yeogiga.application.auth.type.LoginType;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.auth.exception.AuthErrorType;
import kr.co.yeogiga.domain.oauth.entity.OAuth;
import kr.co.yeogiga.domain.oauth.service.OAuthService;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
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

        return getSignInResponse(userStatus);
    }

    /**
     * GUEST 사용자의 권한 승격을 위한 회원 등록 메서드
     *
     * @param userId        사용자 ID
     * @param request       회원 등록 요청 dto (nickname)
     */
    @Transactional
    public void register(Long userId, SignUpDto.Register request) {
        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        if (userService.existsIncludeDeletedByNickname(request.nickname())) {
            throw new CustomException(AuthErrorType.ALREADY_USED_NICKNAME);
        }

        user.updateNickname(request.nickname());
        user.upgradeRoleToUser();
    }

    /**
     * User 및 추가정보 입력 필요 여부 반환 메서드
     *
     * @param platform      OAuth 로그인 플랫폼
     * @param userInfo      OAuth 리소스 서버 제공 사용자 정보
     * @return              User, 추가 정보 입력 필요 여부
     */
    private UserStatusDto getUserStatus(OAuthPlatform platform, UserInfoDto userInfo) {
        return userService.readIncludeDeletedUserByPlatformAndPlatformId(platform, userInfo.platformId())
                .map(user -> {
                    if (!Objects.isNull(user.getDeletedAt())) {
                        user.revertWithdrawal();
                    }

                    return user.isSignedUp()
                            ? UserStatusDto.of(user, false)
                            : UserStatusDto.of(user, true);
                    })
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
                .role(Role.GUEST)
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
     * - 토큰 생성 시, 리프레시 토큰 저장
     *
     * @param userStatus    User, 추가 정보 입력 여부
     * @return              로그인 응답 정보 (JWT, 추가 정보 입력 필요 여부)
     */
    private SignInDto.Response getSignInResponse(UserStatusDto userStatus) {
        User user = userStatus.user();
        String nickname = user.getNickname();
        Long userId = user.getId();

        TokenDto token = jwtService.generateToken(nickname, userId, LoginType.SOCIAL);

        return SignInDto.Response.builder()
                .token(token)
                .shouldSignup(userStatus.shouldSignUp())
                .build();
    }
}