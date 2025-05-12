package kr.co.yeogiga.application.user.dto;

public record UserInfoUpdateReq(
        String nickname,
        String email
) {
}