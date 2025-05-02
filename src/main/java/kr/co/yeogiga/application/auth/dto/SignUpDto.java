package kr.co.yeogiga.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.co.yeogiga.domain.user.entity.User;
import kr.co.yeogiga.domain.user.type.Role;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SignUpDto {
    @Schema(name = "SignUpDto.Request", description = "회원가입 요청 DTO")
    public record Request(
            @Schema(name = "아이디", example = "testid")
            @NotBlank(message = "아이디는 필수 입력값입니다.")
            String username,

            @Schema(name = "비밀번호", example = "testpw")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            String password,

            @Schema(name = "이메일", example = "test@test.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "잘못된 이메일 형식입니다.")
            String email,

            @Schema(name = "닉네임", example = "testnick")
            @NotBlank(message = "닉네임은 필수 입력값입니다.")
            String nickname
    ) {
        public User toUserEntity(String encodedPassword) {
            return User.builder()
                    .username(username)
                    .password(encodedPassword)
                    .email(email)
                    .nickname(nickname)
                    .role(Role.GUEST)
                    .build();
        }
    }
}
