package kr.co.yeogiga.application.auth.dto;

public record UserInfoDto(
        String platformId
) {
    public static UserInfoDto of(String platformId) {
        return new UserInfoDto(platformId);
    }
}