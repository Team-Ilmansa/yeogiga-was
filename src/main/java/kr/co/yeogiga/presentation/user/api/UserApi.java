package kr.co.yeogiga.presentation.user.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[사용자 관련 API]")
@Tag(name = "[사용자 관련 API]", description = "사용지 관련 API")
public interface UserApi {

    @TrackApi(description = "비밀번호 갱신")
    @Operation(summary = "비밀번호 갱신", description = "비밀번호 갱신 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 갱신 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "비밀번호 갱신 성공", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "비밀번호 갱신 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "기존 비밀번호 불일치", value = """
                                        {
                                            "code": "U003",
                                            "message": "비밀번호가 불일치합니다."
                                        }
                                    """),
                            @ExampleObject(name = "유효성 검증 실패", value = """
                                        {
                                            "code": "G002",
                                            "errors": {
                                                "originalPassword": "기존 비밀번호는 필수 입력값입니다."
                                            }
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "비밀번호 갱신 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "토큰 미포함", value = """
                                        {
                                            "code": "A003",
                                            "message": "인증에 실패하였습니다. 토큰을 확인해주세요."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "비밀번호 갱신 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "기존과 동일한 비밀번호", value = """
                                        {
                                            "code": "U002",
                                            "message": "기존과 동일한 비밀번호입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordUpdateReq passwordUpdateReq
    );

    @TrackApi(description = "회원 탈퇴")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "회원 탈퇴 성공", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "회원 탈퇴 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이미 회원 탈퇴한 사용자", value = """
                                        {
                                            "code": "U004",
                                            "message": "이미 회원탈퇴한 사용자입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "회원 탈퇴 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 사용자", value = """
                                        {
                                            "code": "U000",
                                            "message": "존재하지 않는 사용자입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
