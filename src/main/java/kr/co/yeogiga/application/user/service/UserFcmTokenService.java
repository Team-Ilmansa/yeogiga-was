package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.user.dto.FcmTokenReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFcmTokenService {
    private final UserService userService;

    /**
     * 사용자의 FCM 토큰을 등록하거나 갱신하는 메서드
     *
     * @param userId      토큰을 등록할 사용자 ID
     * @param fcmTokenReq 클라이언트에서 전달한 FCM 토큰 요청 객체
     * @throws CustomException UserErrorType.NOT_FOUND - 사용자가 존재하지 않는 경우
     */
    @Transactional
    public void registerFcmToken(Long userId, FcmTokenReq fcmTokenReq) {
        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        user.updateFcmToken(fcmTokenReq.fckToken());
    }

    /**
     * 사용자의 FCM 토큰을 제거 메서드
     * - 로그아웃에 사용
     *
     * @param userId 토큰을 삭제할 사용자 ID
     * @throws CustomException UserErrorType.NOT_FOUND - 사용자가 존재하지 않는 경우
     */
    @Transactional
    public void deleteFcmToken(Long userId) {
        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        user.clearFcmToken();
    }

    /**
     * fcmToken을 통해 사용자의 FCM 토큰을 제거 메서드
     *
     * @param fcmToken 제거할 FcmToken
     */
    @Transactional
    public void deleteFcmTokenByToken(String fcmToken) {
        User user = userService.readByFcmToken(fcmToken)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        user.clearFcmToken();
    }
}
