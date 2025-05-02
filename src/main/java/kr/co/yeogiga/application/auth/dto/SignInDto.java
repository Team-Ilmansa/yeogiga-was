package kr.co.yeogiga.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class SignInDto {

    @Builder
    @Schema(name = "SingInDto.Request", description = "일반 로그인 요청 DTO")
    public record Request(
            @Schema(name = "아이디")
            @NotBlank(message = "아이디는 필수 입력값입니다.")
            String username,

            @Schema(name = "비밀번호")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            String password
    ) {
    }

    @Builder
    @Schema(name = "OAuthRequest", description = "OAuth 로그인 요청 DTO")
    public record OAuthRequest(
            @Schema(description = "OAuth 리소스 서버에서 발급받은 인증 코드", example = "abcd123")
            @NotBlank(message = "인증 코드값은 필수입니다.")
            String code
    ) {
    }

    @Builder
    public record Response(
            TokenDto token,
            boolean shouldSignup
    ) {
        public Response toWebResponse() {
            return Response.builder()
                    .token(TokenDto.builder()
                            .accessToken(this.token.accessToken())
                            .build())
                    .shouldSignup(this.shouldSignup)
                    .build();
        }
    }
}