package kr.co.yeogiga.application.user.service;

import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.common.exception.CustomException;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.exception.UserErrorType;
import kr.co.yeogiga.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateReq passwordReq) {
        User user = userService.readById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND));

        if (passwordEncoder.matches(passwordReq.password(), user.getPassword())) {
            throw new CustomException(UserErrorType.SAME_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(passwordReq.password()));
    }
}
