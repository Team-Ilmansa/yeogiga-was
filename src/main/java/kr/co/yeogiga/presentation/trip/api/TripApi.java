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
import jakarta.validation.Valid;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[여행 API]")
@Tag(name = "[여행 API]", description = "여행 관련 API")
public interface TripApi {

    @TrackApi(description = "메인 화면 내 여행 조회")
    @Operation(summary = "메인 화면 내 여행 조회", description = "메인 화면 내 여행 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메인 화면 내 여행 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "예정인 여행", description = "예정인 여행은 1일차의 정보를 반환", value = """
                                             {
                                                  "code": 200,
                                                  "message": "요청이 성공하였습니다.",
                                                  "data": {
                                                      "tripId": 1,
                                                      "title": "여행1",
                                                      "staredAt": "2025-05-25T12:00:00",
                                                      "travelStatus": "PLANNED",
                                                      "day": 1,
                                                      "places": [
                                                          {
                                                              "id": "place-id",
                                                              "name": "달성공원",
                                                              "placeType": "관광지",
                                                              "isVisited": false
                                                          },
                                                          {
                                                              "id": "place-id",
                                                              "name": "두류공원",
                                                              "placeType": "관광지",
                                                              "isVisited": false
                                                          }
                                                      ]
                                                  }
                                              }
                                    """),
                            @ExampleObject(name = "진행 중인 여행", description = "여행 진행 중인 일자의 정보를 반환", value = """
                                             {
                                                   "code": 200,
                                                   "message": "요청이 성공하였습니다.",
                                                   "data": {
                                                       "tripId": 4,
                                                       "title": "여행4",
                                                       "staredAt": "2025-05-17T12:00:00",
                                                       "travelStatus": "IN_PROGRESS",
                                                       "day": 5,
                                                       "places": [
                                                           {
                                                               "id": "place-id",
                                                               "name": "계명대",
                                                               "placeType": "관광지",
                                                               "isVisited": false
                                                           },
                                                           {
                                                               "id": "place-id",
                                                               "name": "영남대",
                                                               "placeType": "관광지",
                                                               "isVisited": false
                                                           },
                                                           {
                                                               "id": "place-id",
                                                               "name": "두류공원",
                                                               "placeType": "관광지",
                                                               "isVisited": false
                                                           },
                                                           {
                                                               "id": "place-id",
                                                               "name": "달성공원",
                                                               "placeType": "관광지",
                                                               "isVisited": false
                                                           }
                                                       ]
                                                   }
                                               }
                                    """),
                            @ExampleObject(name = "예정이거나 진행 중인 여행이 없는 경우", description = "data 부분 제외", value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> getMainTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
    @TrackApi(description = "사용자가 속한 여행 조회")
    @Operation(summary = "사용자가 속한 여행 조회", description = "사용자가 속한 여행 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자가 속한 여행 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "조회 성공", value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다.",
                                                 "data": [
                                                     {
                                                         "tripId": 2,
                                                         "title": "여행2",
                                                         "startedAt": "2025-05-10T12:00:00",
                                                         "endedAt": "2025-05-20T12:01:00",
                                                         "status": "IN_PROGRESS"
                                                     },
                                                     {
                                                         "tripId": 1,
                                                         "title": "여행1",
                                                         "startedAt": "2025-05-23T12:00:00",
                                                         "endedAt": "2025-05-26T12:01:00",
                                                         "status": "PLANNED"
                                                     },
                                                     {
                                                         "tripId": 3,
                                                         "title": "여행3",
                                                         "startedAt": null,
                                                         "endedAt": null,
                                                         "status": "PLANNED"
                                                     }
                                                 ]
                                             }
                                    """),
                            @ExampleObject(name = "조회 성공 - 속한 여행 없는 경우 빈 배열 반환", value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다.",
                                                 "data": []
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> getAllTrip(@AuthenticationPrincipal CustomUserDetails userDetails);

    @TrackApi(description = "여행 생성")
    @Operation(summary = "여행 생성", description = "여행 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 201,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 생성 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "최대 글자수 초과", value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "title": "제목은 최대 20글자까지 가능합니다."
                                                 }
                                             }
                                    """),
                            @ExampleObject(name = "필수 입력값 미입력", value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "title": "제목은 필수 입력값입니다."
                                                 }
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> createTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TripReq.Creation request
    );

    @TrackApi(description = "여행 정보 수정")
    @Operation(summary = "여행 정보 수정", description = "여행 정보 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 정보 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 정보 수정 실패 - 유효성 검증",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "제목 미입력", value = """
                                             {
                                                  "code": "G002",
                                                  "errors": {
                                                      "title": "제목은 필수 입력값입니다."
                                                  }
                                              }
                                    """),
                            @ExampleObject(name = "제목 글자수 초과", value = """
                                             {
                                                  "code": "G002",
                                                  "errors": {
                                                      "title": "제목은 최대 20글자까지 가능합니다."
                                                  }
                                              }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "여행 정보 수정 실패 - 여행 미존재",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 미존재", value = """
                                             {
                                                 "code": "T006",
                                                 "message": "해당 여행이 존재하지 않습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "여행 정보 수정 실패 - 기존과 동일한 제목",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "기존과 동일한 제목", value = """
                                             {
                                                  "code": "T011",
                                                  "message": "기존과 동일한 여행 제목입니다."
                                              }
                                    """)
                    }))
    })
    ResponseEntity<?> updateTripInfo(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Valid @RequestBody TripReq.Update updateRequest
    );

    @TrackApi(description = "여행 시간 수정")
    @Operation(summary = "여행 시간 수정", description = "여행 시간 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 시간 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 시간 수정 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 시작, 종료 시간 범위 에러 (종료 시각 <= 시작 시간)", value = """
                                             {
                                                 "code": "T008",
                                                 "message": "여행 시작 시간과 종료 시간을 확인하세요."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "여행 시간 수정 실패 - 권한",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "방장이 아닌 사용자 수정 불가", value = """
                                             {
                                                 "code": "T007",
                                                 "message": "여행 방장이 아닙니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "여행 시간 수정 실패 - 여행 존재 여부",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 여행", value = """
                                             {
                                                 "code": "T006",
                                                 "message": "여행 시작 시간과 종료 시간을 확인하세요."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> updateTripTime(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Valid
            @RequestBody TripReq.Time request
    );

    @TrackApi(description = "여행 삭제")
    @Operation(summary = "여행 삭제", description = "여행 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> removeTrip(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId);
}
