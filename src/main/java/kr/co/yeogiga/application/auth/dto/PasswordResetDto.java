package kr.co.yeogiga.application.auth.dto;

public class PasswordResetDto {
    public record Request(
            String email,
            String username
    ) { }
}
