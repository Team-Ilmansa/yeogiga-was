package kr.co.yeogiga.presentation.auth.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.yeogiga.application.auth.dto.PasswordResetDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[비밀번호 API]")
@Tag(name = "[비밀번호 API]", description = "비밀번호 관련 API")
public interface PasswordApi {
    
    @TrackApi(description = "비밀번호 초기화 요청")
    @Operation(summary = "비밀번호 초기화 요청", description = "비밀번호 초기화 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 초기화 요청 성공, 성공 후 해당 이메일로 비밀번호 초기화 메일 발송",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "비밀번호 초기화 요청 성공", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "비밀번호 초기화 요청 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검증 실패", value = """
                                        {
                                            "code": "G002",
                                            "errors": {
                                                "email": "잘못된 이메일 형식입니다.",
                                                "username": "아이디는 필수 입력값입니다."
                                            }
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetDto.Request request);
    
    @TrackApi(description = "비밀번호 초기화")
    @Operation(summary = "비밀번호 초기화", description = "비밀번호 초기화 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 초기화 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "비밀번호 초기화 성공", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "비밀번호 초기화 요청 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검증 실패", value = """
                                        {
                                            "code": "G002",
                                            "errors": {
                                                "email": "잘못된 이메일 형식입니다.",
                                                "username": "아이디는 필수 입력값입니다."
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "비밀번호 초기화 시간 초과", value = """
                                        {
                                             "code": "A019",
                                             "message": "비밀번호 초기화 요청 시간이 초과하였습니다. 다시 시도해주세요."
                                         }
                                    """),
                            @ExampleObject(name = "비밀번호 확인용 코드 불일치", value = """
                                        {
                                             "code": "A020",
                                             "message": "비밀번호 초기화 인증 코드가 일치하지 않습니다."
                                         }
                                    """)
                    }))
    })
    ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDto.Reset request);
}
