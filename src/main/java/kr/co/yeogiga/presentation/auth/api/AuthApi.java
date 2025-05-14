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
import kr.co.yeogiga.application.auth.dto.SignInDto;
import kr.co.yeogiga.application.auth.dto.SignUpDto;
import kr.co.yeogiga.application.auth.type.Device;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@ApiGroup(value = "[인증 API]")
@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthApi {

    @TrackApi(description = "회원가입")
    @Operation(summary = "회원가입", description = "회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "회원가입 성공", value = """
                                        {
                                             "code": 201,
                                             "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검증 실패", description = "이메일의 경우 입력 여부 및 형식 검사", value = """
                                        {
                                            "code": "G002",
                                            "errors": {
                                                "password": "비밀번호는 필수 입력값입니다.",
                                                "nickname": "닉네임은 필수 입력값입니다.",
                                                "email": "잘못된 이메일 형식입니다.",
                                                "username": "아이디는 필수 입력값입니다."
                                            }
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "회원가입 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "회원가입 실패 - 이미 존재하는 아이디", value = """
                                        {
                                            "code": "A011",
                                            "message": "이미 사용 중인 아이디입니다."
                                        }
                                    """),
                            @ExampleObject(name = "회원가입 실패 - 이미 존재하는 아이디", value = """
                                        {
                                            "code": "A012",
                                            "message": "이미 사용 중인 닉네임입니다."
                                        }
                                    """),
                            @ExampleObject(name = "회원가입 실패 - 이미 존재하는 아이디", value = """
                                        {
                                            "code": "A013",
                                            "message": "이미 사용 중인 이메일입니다."
                                        }
                                    """)
                    }))
    })
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto.Request request);

    @TrackApi(description = "일반 로그인")
    @Operation(summary = "일반 로그인", description = "일반 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "로그인 성공(웹)", description = "웹은 리프레시 토큰이 쿠키로 전달", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "accessToken": "xxxxx.xxxxx.xxxxx"
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "로그인 성공(모바일)", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다.",
                                             "data": {
                                                 "accessToken": "xxxxx.xxxxx.xxxxx",
                                                 "refreshToken": "xxxxx.xxxxx.xxxxx"
                                             }
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검증 실패", value = """
                                        {
                                             "code": "G002",
                                             "errors": {
                                                 "password": "비밀번호는 필수 입력값입니다.",
                                                 "username": "아이디는 필수 입력값입니다."
                                             }
                                        }
                                    """)
                    }))
    })
    public ResponseEntity<?> signIn(
            @RequestHeader(name = "device") Device device,
            @Valid @RequestBody SignInDto.Request request
    );

    @TrackApi(description = "토큰 재발급")
    @Operation(summary = "토큰 재발급", description = "토큰(access token, refresh token) 재발급 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "토큰 재발급 성공(웹)", description = "웹은 리프레시 토큰이 쿠키로 전달", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "accessToken": "xxxxx.xxxxx.xxxxx"
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "토큰 재발급 성공(모바일)", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다.",
                                             "data": {
                                                 "accessToken": "xxxxx.xxxxx.xxxxx",
                                                 "refreshToken": "xxxxx.xxxxx.xxxxx"
                                             }
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "리프레시 토큰이 없을 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "리프레시 토큰 미포함", value = """
                                        {
                                             "code": "A009",
                                             "message": "리프레시 토큰을 찾을 수 없습니다."
                                        }
                                    """),
                            @ExampleObject(name = "리프레시 토큰 만료", description = "리프레시 토큰이 만료되어 Redis 내에서 삭제된 경우", value = """
                                        {
                                            "code": "A008",
                                            "message": "리프레시 토큰이 만료되었습니다. 재로그인 해주세요."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> reissueToken(
            @RequestHeader(name = "device") Device device,
            @RequestHeader(name = "refreshToken", required = false) String refreshTokenInHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshTokenInCookie
    );

    @TrackApi(description = "로그아웃")
    @Operation(summary = "로그아웃", description = "로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "로그아웃 성공", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "로그아웃 실패 - 인증",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Authorization 헤더 미포함", value = """
                                        {
                                            "code": "A003",
                                            "message": "인증에 실패하였습니다. 토큰을 확인해주세요."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> signOut(
            @RequestHeader(name = "device") Device device,
            @RequestHeader(name = "refreshToken", required = false) String refreshTokenInHeader,
            @CookieValue(name = "refreshToken", required = false) String refreshTokenInCookie
    );

    @TrackApi(description = "아이디 중복 확인")
    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용가능한 아이디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "사용가능한 아이디", value = """
                                        {
                                            "code": 200,
                                            "message": "사용 가능한 아이디입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이미 사용 중인 아이디", value = """
                                        {
                                            "code": "A011",
                                            "message": "이미 사용 중인 아이디입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> checkDuplicatedUsername(@RequestParam(name = "value") String username);

    @TrackApi(description = "닉네임 중복 확인")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 확인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용가능한 닉네임",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "사용가능한 닉네임", value = """
                                        {
                                            "code": 200,
                                            "message": "사용 가능한 닉네임입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이미 사용 중인 닉네임", value = """
                                        {
                                            "code": "A012",
                                            "message": "이미 사용 중인 닉네임입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> checkDuplicatedNickname(@RequestParam(name = "value") String nickname);
}
