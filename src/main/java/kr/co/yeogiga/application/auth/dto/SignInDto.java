package kr.co.yeogiga.application.auth.dto;

import lombok.Builder;

public class SignInDto {

    @Builder
    public record Response(
            TokenDto token,
            boolean shouldSignup
    ) {
    }
}