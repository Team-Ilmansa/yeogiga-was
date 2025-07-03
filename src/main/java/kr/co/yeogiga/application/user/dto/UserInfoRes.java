package kr.co.yeogiga.application.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.yeogiga.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserInfoRes(
        String username,
        String nickname,
        String email,
        String imageUrl
) {
    public static UserInfoRes fromNormalUser(User user) {
        return UserInfoRes.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();
    }

    public static UserInfoRes fromSocialUser(User user) {
        return UserInfoRes.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
