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
import kr.co.yeogiga.application.auth.type.Device;
import kr.co.yeogiga.domain.oauth.type.OAuthPlatform;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@ApiGroup(value = "[OAuth2 인증 API]")
@Tag(name = "[OAuth2 인증 API]", description = "OAuth2 인증 관련 API")
public interface OAuthApi {

    @TrackApi(description = "OAuth2 로그인")
    @Operation(summary = "OAuth2 로그인", description = "OAuth2를 통한 로그인 API 입니다. 처음 로그인하는 사용자는 '여기가'에 유저 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "기존 회원가입된 사용자(모바일)", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "token": {
                                                    "accessToken": "xxxxx.xxxxx.xxxxx",
                                                    "refreshToken": "xxxxx.xxxxx.xxxxx"
                                                },
                                                "shouldSignup": false
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "기존 회원가입되지 않은 사용자(모바일)",  description = "추가 정보 입력(회원가입) 후 토큰 갱신 필요", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "token": {
                                                    "accessToken": "xxxxx.xxxxx.xxxxx",
                                                    "refreshToken": "xxxxx.xxxxx.xxxxx"
                                                },
                                                "shouldSignup": true
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
                                    """),
                            @ExampleObject(name = "기존 회원가입되지 않은 사용자(웹)", description = "추가 정보 입력(회원가입) 후 토큰 갱신 필요 / 웹은 refresh token이 쿠키로 전달", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다.",
                                             "data": {
                                                 "token": {
                                                     "accessToken": "xxxxx.xxxxx.xxxxx"
                                                 },
                                                 "shouldSignup": true
                                             }
                                         }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "인증 코드 유효성 검사 실패", value = """
                                        {
                                            "code": "G002",
                                            "errors": {
                                                "code": "인증 코드값은 필수입니다."
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "플랫폼 유효성 검사 실패", value = """
                                        {
                                            "code": "G003",
                                            "message": "지원하지 않는 Path Variable 값입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> signIn(@RequestHeader(value = "device") Device device, @PathVariable(name = "platform") OAuthPlatform platform, @Valid @RequestBody SignInDto.OAuthRequest request);
}