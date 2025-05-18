package kr.co.yeogiga.presentation.calendar.api;

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
import kr.co.yeogiga.application.calendar.dto.CalendarReq;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[W2M API]")
@Tag(name = "[W2M API]", description = "W2M 관련 API")
public interface CalendarApi {

    @TrackApi(description = "W2M 생성")
    @Operation(summary = "W2M 생성", description = "W2M 생성하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "W2M 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 201,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "W2M 생성 유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "availableDates": "가능한 날짜 목록은 비어 있을 수 없습니다."
                                                 }
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                             "code": "T006",
                                             "message": "해당 여행이 존재하지 않습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                             "code": "T010",
                                             "message": "이미 가능한 날짜 정보가 등록되어 있습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> createCalendar(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Valid @RequestBody CalendarReq calendarReq
    );

    @TrackApi(description = "W2M 조회 (개인 선택 날짜)")
    @Operation(summary = "W2M 조회 (개인 선택 날짜)", description = "W2M 조회 (개인 선택 날짜)하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "W2M 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                    "userId": 1,
                                                    "availableDates": [
                                                        "2025-07-01",
                                                        "2025-07-02"
                                                    ]
                                            }
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                             "code": "T009",
                                             "message": "사용자의 가능한 날짜 정보가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getUserAvailability(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );

    @TrackApi(description = "W2M 조회 (팀 전체)")
    @Operation(summary = "W2M 조회 (팀 전체)", description = "W2M 조회 (팀 전체)하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "W2M 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                    "availabilities": [
                                                        {
                                                            "userId": 1,
                                                            "availableDates": [
                                                                "2025-07-01",
                                                                "2025-07-02"
                                                            ]
                                                        },
                                                        {
                                                            "userId": 2,
                                                            "availableDates": [
                                                                "2025-07-01",
                                                                "2025-07-10",
                                                                "2025-07-19"
                                                            ]
                                                        }
                                                    ]
                                            }
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getTripAvailabilities(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );

    @TrackApi(description = "W2M 수정")
    @Operation(summary = "W2M 수정", description = "W2M 수정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "W2M 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "W2M 생성 유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "availableDates": "가능한 날짜 목록은 비어 있을 수 없습니다."
                                                 }
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                             "code": "T009",
                                             "message": "사용자의 가능한 날짜 정보가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> updateAvailableDates(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Valid @RequestBody CalendarReq calendarReq
    );
}
