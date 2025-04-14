package kr.co.yeogiga.application.auth.dto;

import kr.co.yeogiga.domain.user.entity.User;

public record UserStatusDto(
        User user,
        boolean shouldSignUp
) {
    public static UserStatusDto of(User user, boolean shouldSignUp) {
        return new UserStatusDto(user, shouldSignUp);
    }
}
