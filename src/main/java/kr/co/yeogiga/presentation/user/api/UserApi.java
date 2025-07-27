package kr.co.yeogiga.presentation.user.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.yeogiga.application.user.dto.FcmTokenReq;
import kr.co.yeogiga.application.user.dto.PasswordUpdateReq;
import kr.co.yeogiga.application.user.dto.UserInfoUpdateReq;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
                                            "code": "U002",
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
                                            "code": "U001",
                                            "message": "기존과 동일한 비밀번호입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
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
                                            "code": "U003",
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
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails
    );

    @TrackApi(description = "회원 정보 조회")
    @Operation(summary = "회원 정보 조회", description = "회원 정보 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "소셜 로그인 사용자", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "username": null,
                                                "nickname": "test",
                                                "email": "test@test.com",
                                                "imageUrl": "https://image.com"
                                            }
                                        }
                                    """),
                            @ExampleObject(name = "일반 로그인 사용자", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "username": "test",
                                                "nickname": "test",
                                                "email": "test@test.com",
                                                "imageUrl": "https://image.com"
                                            }
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
    ResponseEntity<?> getUserInfo(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails
    );

    @TrackApi(description = "회원 정보 수정")
    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "성공", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "회원 정보 수정 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이미 사용 중인 닉네임", value = """
                                        {
                                            "code": "U004",
                                            "message": "이미 사용 중인 닉네임입니다."
                                        }
                                    """),
                            @ExampleObject(name = "기존과 동일한 닉네임", value = """
                                        {
                                            "code": "U005",
                                            "message": "기존과 동일한 닉네임 입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> update(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody UserInfoUpdateReq userInfoUpdateReq
    );

    @TrackApi(description = "사용자 프로필 등록 및 수정")
    @Operation(summary = "사용자 프로필 등록 및 수정", description = "사용자 프로필 등록 및 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 프로필 등록 및 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "성공", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "회원 정보 수정 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이미지를 첨부하지 않은 경우", value = """
                                        {
                                             "code": "I002",
                                             "message": "이미지는 필수 입력값입니다."
                                         }
                                    """)
                    }))
    })
    ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,

            @Parameter(description = "업로드할 이미지")
            @RequestPart(name = "image") MultipartFile image
    );

    @TrackApi(description = "Fcm Token 저장")
    @Operation(summary = "Fcm Token 저장", description = "Fcm Token 저장 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fcm Token 저장 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Fcm Token 저장 성공", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "Fcm Token 저장 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 사용자", value = """
                                        {
                                            "code": "U000",
                                            "message": "존재하지 않는 사용자입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> registerFcmToken(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @RequestBody FcmTokenReq fcmTokenReq
    );

    @TrackApi(description = "Fcm Token 삭제")
    @Operation(summary = "Fcm Token 삭제", description = "Fcm Token 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fcm Token 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Fcm Token 삭제 성공", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "Fcm Token 삭제 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 사용자", value = """
                                        {
                                            "code": "U000",
                                            "message": "존재하지 않는 사용자입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteFcmToken(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails
    );
}
