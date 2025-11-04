package kr.co.yeogiga.presentation.test;

import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.infrastructure.fcm.FcmNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FcmTestController {
    private final FcmNotificationSender fcmNotificationSender;

    @GetMapping("/fcm-test")
    public ResponseEntity<?> fcmTest(@RequestParam String token) {
        fcmNotificationSender.sendNotification(token, "테스트", "테스트 알림", Map.of());
        return ResponseEntity.ok(SuccessResponse.ok());
    }

}
