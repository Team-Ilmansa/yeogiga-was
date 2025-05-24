package kr.co.yeogiga.presentation.trip.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@ApiGroup(value = "[여행 멤버 API]")
@Tag(name = "[여행 멤버 API]", description = "여행 멤버 관련 API")
public interface TripMemberApi {

    @TrackApi(description = "여행 멤버 조회")
    @Operation(summary = "여행 멤버 조회", description = "여행 멤버 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 멤버 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(description = "이미지 미존재 멤버의 경우에는 모노그램 이미지로 표시 필요", value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다.",
                                                 "data": [
                                                     {
                                                         "userId": 1,
                                                         "nickname": "nick1",
                                                         "imageUrl": "http://image1.com"
                                                     },
                                                     {
                                                         "userId": 2,
                                                         "nickname": "nick2",
                                                         "imageUrl": "http://image2.com"
                                                     },
                                                     {
                                                         "userId": 3,
                                                         "nickname": "nick3",
                                                         "imageUrl": null
                                                     },
                                                     {
                                                         "userId": 4,
                                                         "nickname": "nick4",
                                                         "imageUrl": null
                                                     }
                                                 ]
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> getTripMembers(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );

    @TrackApi(description = "여행 멤버 참가")
    @Operation(summary = "여행 멤버 참가", description = "여행 멤버 참가 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 멤버 참가 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 201,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "여행 참가 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 여행", value = """
                                             {
                                                 "code": "T006",
                                                 "message": "해당 여행이 존재하지 않습니다."
                                             }
                                    """),
                            @ExampleObject(name = "존재하지 않는 사용자", value = """
                                             {
                                                 "code": "U000",
                                                 "message": "존재하지 않는 사용자입니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "여행 참가 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "이미 참가 중인 사용자", value = """
                                             {
                                                 "code": "T100",
                                                 "message": "이미 여행에 참가 중인 사용자입니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> joinTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );

    @TrackApi(description = "여행 탈퇴")
    @Operation(summary = "여행 탈퇴", description = "여행 탈퇴 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 탈퇴 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 탈퇴 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 멤버가 2명 이상이고, 요청자가 방장인 경우", value = """
                                             {
                                                  "code": "T101",
                                                  "message": "방장은 여행에서 떠날 수 없습니다. 권한을 위임해주세요."
                                              }
                                    """),
                            @ExampleObject(name = "여행 멤버가 아닌 경우", value = """
                                             {
                                                  "code": "T102",
                                                  "message": "해당 여행의 멤버가 아닙니다."
                                              }
                                    """)
                    }))
    })
    ResponseEntity<?> leaveTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );

    @TrackApi(description = "여행 멤버 추방")
    @Operation(summary = "여행 멤버 추방", description = "여행 멤버 추방 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 멤버 추방 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 멤버 추방 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "자기 자신을 추방하려 하는 경우", value = """
                                             {
                                                   "code": "T103",
                                                   "message": "자기 자신은 추방할 수 없습니다."
                                               }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "여행 멤버 추방 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 방장이 아닌 사용자가 요청한 경우", value = """
                                             {
                                                 "code": "T104",
                                                 "message": "여행 방장만 이용 가능한 기능입니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> leaveTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "추방 멤버 ID")
            @PathVariable Long memberId
    );
}
