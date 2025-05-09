package kr.co.yeogiga.presentation.tripplace.image.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[이미지 위치 이동 API]")
@Tag(name = "[이미지 위치 이동 API]", description = "이미지 위치 이동 관련 API")
public interface TripPlaceImageMovementApi {

    @TrackApi(description = "같은 날짜 목적지 to 목적지")
    @Operation(summary = "같은 날짜 목적지 to 목적지", description = "같은 날짜 목적지 to 목적지 이동하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 이동 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 일차 존재하지 않음", value = """
                                        {
                                            "code": "T003",
                                            "message": "해당 여행 일차 정보가 존재하지 않습니다."
                                        }
                                    """),
                            @ExampleObject(name = "목적지 존재하지 않음", value = """
                                        {
                                             "code": "T005",
                                             "message": "해당 목적지가 존재하지 않습니다"
                                        }
                                    """),
                            @ExampleObject(name = "이미지 존재하지 않음", value = """
                                        {
                                             "code": "I001",
                                             "message": "해당 이미지가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> moveImageToAnotherPlace(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageDto.ImageMoveReq imageReq
    );

    @TrackApi(description = "다른 날짜 목적지 to 목적지")
    @Operation(summary = "다른 날짜 목적지 to 목적지", description = "다른 날짜 목적지 to 목적지 이동하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 이동 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 일차 존재하지 않음", value = """
                                        {
                                            "code": "T003",
                                            "message": "해당 여행 일차 정보가 존재하지 않습니다."
                                        }
                                    """),
                            @ExampleObject(name = "목적지 존재하지 않음", value = """
                                        {
                                             "code": "T005",
                                             "message": "해당 목적지가 존재하지 않습니다"
                                        }
                                    """),
                            @ExampleObject(name = "이미지 존재하지 않음", value = """
                                        {
                                             "code": "I001",
                                             "message": "해당 이미지가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> moveImageBetweenDayPlaces(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageDto.ImageCrossDayMoveReq imageReq
    );

    @TrackApi(description = "같은 날짜 목적지 to unmatched(기타)")
    @Operation(summary = "같은 날짜 목적지 to unmatched(기타)", description = "같은 날짜 목적지 to unmatched(기타) 이동하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 이동 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 일차 존재하지 않음", value = """
                                        {
                                            "code": "T003",
                                            "message": "해당 여행 일차 정보가 존재하지 않습니다."
                                        }
                                    """),
                            @ExampleObject(name = "목적지 존재하지 않음", value = """
                                        {
                                             "code": "T005",
                                             "message": "해당 목적지가 존재하지 않습니다"
                                        }
                                    """),
                            @ExampleObject(name = "이미지 존재하지 않음", value = """
                                        {
                                             "code": "I001",
                                             "message": "해당 이미지가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> moveImageToUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageDto.ImageUnmatchedMoveReq imageReq
    );

    @TrackApi(description = "같은 날짜 unmatched(기타) to 목적지")
    @Operation(summary = "같은 날짜 unmatched(기타) to 목적지", description = "같은 날짜 unmatched(기타) to 목적지 이동하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 이동 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 일차 존재하지 않음", value = """
                                        {
                                            "code": "T003",
                                            "message": "해당 여행 일차 정보가 존재하지 않습니다."
                                        }
                                    """),
                            @ExampleObject(name = "목적지 존재하지 않음", value = """
                                        {
                                             "code": "T005",
                                             "message": "해당 목적지가 존재하지 않습니다"
                                        }
                                    """),
                            @ExampleObject(name = "이미지 존재하지 않음", value = """
                                        {
                                             "code": "I001",
                                             "message": "해당 이미지가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> moveImageFromUnmatched(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @RequestBody TripPlaceImageDto.ImageUnmatchedMoveReq imageReq
    );
}
