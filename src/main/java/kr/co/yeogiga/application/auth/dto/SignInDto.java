package kr.co.yeogiga.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SignInDto {

    @Builder
    @Schema(name = "SingInDto.Request", description = "일반 로그인 요청 DTO")
    public record Request(
            @Schema(description = "아이디", example = "testid")
            @NotBlank(message = "아이디는 필수 입력값입니다.")
            String username,

            @Schema(description = "비밀번호", example = "testpw")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            String password
    ) {
    }

    
    public static class OAuthRequest {
        @Builder
        @Schema(name = "OAuthRequest.Web", description = "웹 이용자 OAuth 로그인 요청 DTO")
        public record Web(
                @Schema(description = "OAuth 리소스 서버에서 발급받은 인증 코드", example = "abcd123")
                @NotBlank(message = "인증 코드값은 필수입니다.")
                String code
        ) { }
        
        @Builder
        @Schema(name = "OAuthRequest.Mobile", description = "모바일 이용자 OAuth 로그인 요청 DTO")
        public record Mobile(
                @Schema(description = "OAuth 리소스 서버에서 발급받은 액세스 토큰", example = "xxx.xxx.xxx")
                @NotBlank(message = "액세스 토큰 값은 필수입니다.")
                String accessToken
        ) { }
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
    
    @Builder
    public record WithdrawnUserInfo(
            Long userId,
            LocalDate deletionExpiration
    ) {
        public static WithdrawnUserInfo of(Long userId, LocalDateTime deletedAt) {
            return WithdrawnUserInfo.builder()
                    .userId(userId)
                    .deletionExpiration(deletedAt.toLocalDate().plusDays(7))
                    .build();
        }
    }
}