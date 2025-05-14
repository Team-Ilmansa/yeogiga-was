package kr.co.yeogiga.presentation.tripplace.image.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageDeleteDto;
import kr.co.yeogiga.application.tripplace.image.dto.TripPlaceImageReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[이미지 매칭 후 이미지 API]")
@Tag(name = "[이미지 매칭 후 이미지 API]", description = "이미지 매칭 후 이미지 관련 API")
public interface TripPlaceImageApi {

    @TrackApi(description = "목적지에 맞는 이미지 조회")
    @Operation(summary = "목적지에 맞는 이미지 조회", description = "목적지에 맞는 이미지 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목적지에 맞는 이미지 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "id": "place1-id",
                                                "name": "목적지1",
                                                "latitude": 0.0,
                                                "longitude": 1.1,
                                                "type": "식당",
                                                "images": [
                                                     {
                                                         "id": "image1-id",
                                                         "url": "https://image1.com",
                                                         "latitude": 1.1,
                                                         "longitude": 2.2,
                                                         "date": "2025-04-13T21:53:57.445"
                                                     }
                                                ]
                                            }
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "여행 일차 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": "T005",
                                            "message": "해당 목적지가 존재하지 않습니다"
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getPlaceInfo(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @PathVariable String placeId
    );

    @TrackApi(description = "기타 이미지 조회")
    @Operation(summary = "기타 이미지 조회", description = "기타 이미지 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기타 이미지 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": {
                                                "images": [
                                                     {
                                                         "id": "image1-id",
                                                         "url": "https://image1.com"
                                                     }
                                                ]
                                            }
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getUnmatchedImageInfo(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId
    );


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
            @RequestBody TripPlaceImageReq.ImageMove imageReq
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
            @RequestBody TripPlaceImageReq.ImageCrossDayMove imageReq
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
            @RequestBody TripPlaceImageReq.ImageUnmatchedMove imageReq
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
            @RequestBody TripPlaceImageReq.ImageUnmatchedMove imageReq
    );

    @TrackApi(description = "이미지 단일 삭제")
    @Operation(summary = "이미지 단일 삭제", description = "이미지 단일 삭제 API입니다." +
            " 목적지 내 삭제인지, 기타 항목 삭제인지 요청값 DeleteType를 적절히 적어주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 단일 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> deleteSingleImage(
            @PathVariable Long tripId,
            @PathVariable String tripDayPlaceId,
            @PathVariable String imageId,
            @RequestBody TripPlaceImageDeleteDto.SingleDeleteReq deleteReq
    );

    @TrackApi(description = "이미지 벌크 삭제")
    @Operation(summary = "이미지 벌크 삭제", description = "이미지 벌크 삭제 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 단일 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
    })
    ResponseEntity<?> deleteMultipleImages(
            @PathVariable Long tripId,
            @RequestBody TripPlaceImageDeleteDto.MultiDeleteReq deleteReq
    );
}
