package kr.co.yeogiga.application.auth.dto;

public record UserInfoDto(
        String platformId,
        String email
) {
    public static UserInfoDto of(String platformId, String email) {
        return new UserInfoDto(platformId, email);
    }
}