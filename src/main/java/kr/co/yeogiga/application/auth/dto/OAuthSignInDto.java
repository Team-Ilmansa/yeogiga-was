package kr.co.yeogiga.application.auth.dto;

public class OAuthSignInDto {
    public record Request(
            String code
    ) { }
}