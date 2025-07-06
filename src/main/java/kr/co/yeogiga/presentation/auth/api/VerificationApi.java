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
import kr.co.yeogiga.application.auth.dto.VerificationCodeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[이메일 인증 API]")
@Tag(name = "[이메일 인증 API]", description = "이메일 인증 관련 API")
public interface VerificationApi {
    
    @TrackApi(description = "이메일 인증 번호 발송 요청")
    @Operation(summary = "이메일 인증 번호 발송 요청", description = "이메일 인증 번호 발송 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이메일 인증 번호 발송 성공", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검증 실패", description = "이메일 입력 여부 및 형식 검사", value = """
                                        {
                                            "code": "G002",
                                            "errors": {
                                                "email": "잘못된 이메일 형식입니다."
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "이메일 인증 요청 시도 횟수 초과", description = "발송 후 1분 이내 재요청한 경우", value = """
                                        {
                                            "code": "A016",
                                            "message": "이메일 인증 시도 횟수를 초과하였습니다. 잠시 후 시도해주세요."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> sendEmailVerificationCode(@Valid @RequestBody VerificationCodeDto.SendRequest request);
    
    @TrackApi(description = "이메일 인증 번호 검증 요청")
    @Operation(summary = "이메일 인증 번호 검증 요청", description = "이메일 인증 번호 검증 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이메일 인증 번호 검증 성공", value = """
                                        {
                                             "code": 200,
                                             "message": "인증에 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "검증 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검증 실패", description = "인증코드, 이메일 입력 여부 및 형식 검사", value = """
                                        {
                                            "code": "G002",
                                            "errors": {
                                                "email": "잘못된 이메일 형식입니다.",
                                                "code": "인증 번호는 필수 입력값입니다."
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "이메일 인증 시간 초과 및 저장소 내 이메일 미존재", value = """
                                        {
                                             "code": "A014",
                                             "message": "이메일 인증 시간을 초과하였습니다."
                                         }
                                    """),
                            @ExampleObject(name = "이메일 인증 번호 불일치", value = """
                                        {
                                              "code": "A015",
                                              "message": "이메일 인증 번호가 일치하지 않습니다."
                                         }
                                    """)
                    }))
    })
    ResponseEntity<?> verifyEmailVerificationCode(@Valid @RequestBody VerificationCodeDto.VerificationRequest request);
}
