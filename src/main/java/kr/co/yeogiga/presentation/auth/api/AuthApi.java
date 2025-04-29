package kr.co.yeogiga.presentation.auth.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.auth.type.Device;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;

@ApiGroup(value = "[인증 API]")
@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthApi {

    @TrackApi(description = "토큰 재발급")
    @Operation(summary = "토큰 재발급", description = "토큰(access token, refresh token) 재발급 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "토큰 재발급 성공(웹)", value = """
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
                                    """),
                            @ExampleObject(name = "기존 회원가입된 사용자(웹)", description = "웹은 refresh token이 쿠키로 전달", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다.",
                                             "data": {
                                                 "token": {
                                                     "accessToken": "xxxxx.xxxxx.xxxxx"
                                                 },
                                                 "shouldSignup": false
                                             }
                                         }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "쿠키에 리프레시 토큰이 없을 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "쿠키 내 리프레시 토큰 미포함", value = """
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
            @RequestHeader(value = "device") Device device,
            @CookieValue(name = "refreshToken", required = false) String refreshToken
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
    ResponseEntity<?> signOut(@CookieValue(name = "refreshToken", required = false) String refreshToken);

}
