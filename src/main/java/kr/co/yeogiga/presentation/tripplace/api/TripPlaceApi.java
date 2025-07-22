package kr.co.yeogiga.presentation.tripplace.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.tripplace.dto.TripPlaceReq;
import kr.co.yeogiga.application.tripplace.dto.VisitedMarkReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[여행 목적지 API]")
@Tag(name = "[여행 목적지 API]", description = "여행 목적지 관련 API")
public interface TripPlaceApi {

    @TrackApi(description = "일차에 담은 여행 목적지 확정")
    @Operation(summary = "일차에 담은 여행 목적지 확정", description = "일차에 담은 여행 목적지를 확정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 목적지 확정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 201,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> completeTrip(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행의 마지막 일차 (3일 여행이라면 3)")
            @RequestBody TripPlaceReq.CompleteRequest request
    );

    @TrackApi(description = "여행 목적지 추가 - 하나씩 (여행 목적지 지정 확정 후)")
    @Operation(summary = "여행 목적지 추가 - 하나씩 (여행 목적지 지정 확정 후)", description = "여행 목적지를 추가하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 목적지 추가 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 201,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> addNewPlace(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId,

            @Parameter(description = "추가할 목적지 정보")
            @RequestBody TripPlaceReq.Request request
    );

    @TrackApi(description = "여행 일정 정보 불러오기 (여행 목적지 지정 확정 후)")
    @Operation(summary = "여행 일정 정보 불러오기 (여행 목적지 지정 확정 후)", description = "여행 일정 정보를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 일정 정보 불러오기",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": [
                                                    {
                                                        "id": "tripDayPlace1-id",
                                                        "day": 1,
                                                        "places": [
                                                            {
                                                                "id": "place1-id",
                                                                "name": "목적지1",
                                                                "placeType": "식당",
                                                                "isVisited": true
                                                            },
                                                            {
                                                                "id": "place2-id",
                                                                "name": "목적지2",
                                                                "placeType": "식당",
                                                                "isVisited": false
                                                            }
                                                        ]
                                                    },
                                                    {
                                                        "id": "tripDayPlace2-id",
                                                        "day": 2,
                                                        "places": [
                                                            {
                                                                "id": "place3-id",
                                                                "name": "목적지3",
                                                                "placeType": "식당",
                                                                "isVisited": false
                                                            },
                                                            {
                                                                "id": "place4-id",
                                                                "name": "목적지4",
                                                                "placeType": "식당",
                                                                "isVisited": false
                                                            }
                                                        ]
                                                    }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getTripDayPlacesInfo(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );

    @TrackApi(description = "특정 일차 목적지 상세 정보(위도, 경도 포함) 불러오기 (여행 목적지 지정 확정 후)")
    @Operation(summary = "특정 일차 목적지 상세 정보(위도, 경도 포함) 불러오기 (여행 목적지 지정 확정 후)", description = "여행 목적지 상세 정보를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 목적지 상세 정보 불러오기",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": [
                                                    {
                                                        "id": "place1-id",
                                                        "name": "목적지1",
                                                        "latitude": 11.22,
                                                        "longitude": 33.44,
                                                        "placeType": "식당",
                                                        "isVisited": true
                                                    },
                                                    {
                                                        "id": "place2-id",
                                                        "name": "목적지 2",
                                                        "latitude": 55.66,
                                                        "longitude": 77.88,
                                                        "placeType": "식당",
                                                        "isVisited": false
                                                    }
                                                ]
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "여행 일차 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": "T003",
                                            "message": "해당 여행 일차 정보가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getPlaceDetailsInfo(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId
    );

    @TrackApi(description = "목적지 요약 정보 불러오기 (여행 회고 과정 중)")
    @Operation(summary = "목적지 요약 정보 불러오기 (여행 회고 과정 중)", description = "여행 목적지 요약 정보를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 목적지 요약 정보 불러오기",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": [
                                                {
                                                     "id": "tripDayPlace-id",
                                                     "day": 1,
                                                     "places": [
                                                         {
                                                             "id": "place1-id",
                                                             "name": "목적지1",
                                                             "latitude": 0.0,
                                                             "longitude": 1.1,
                                                             "type": "식당",
                                                             "image": {
                                                                     "id": "image1-id",
                                                                     "url": "https://image1.com",
                                                                     "latitude": 1.1,
                                                                     "longitude": 2.2,
                                                                     "date": "2025-04-13T21:53:57.445"
                                                                 }
                                                         },
                                                         {
                                                             "id": "place2-id",
                                                             "name": "목적지2",
                                                             "latitude": 3.3,
                                                             "longitude": 4.4,
                                                             "type": "식당",
                                                             "image": null
                                                         }
                                                     ],
                                                     "unmatchedImage": {
                                                         "id": "image2-id",
                                                         "url": "https://image2.com"
                                                     }
                                                }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getTripDaySummaries(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );

    @TrackApi(description = "일차별 여행 목적지 순서 변경 (여행 목적지 지정 확정 후)")
    @Operation(summary = "일차별 여행 목적지 순서 변경 (여행 목적지 지정 확정 후)", description = "일차별 여행 목적지 순서를 변경하는 API입니다." +
            "<br/> 변경된 순서로의 일차별 목적지 id들을 리스트 형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 목적지 순서 변경 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "여행 일차 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": "T003",
                                            "message": "해당 여행 일차 정보가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> reorderPlaces(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId,

            @Parameter(description = "목적지 순서 변경을 위한 정보")
            @RequestBody TripPlaceReq.ReorderRequest reorderRequest
    );

    @TrackApi(description = "여행 목적지 방문 여부 체크 (여행 목적지 지정 확정 후)")
    @Operation(summary = "여행 목적지 방문 여부 체크 (여행 목적지 지정 확정 후)", description = "여행 목적지 방문 여부 체크하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 목적지 방문 여부 체크 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> markPlaceAsVisited(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId,

            @Parameter(description = "목적지  ID")
            @PathVariable String placeId,

            @RequestBody VisitedMarkReq visitedMarkReq
    );

    @TrackApi(description = "특정 일차 여행 목적지 삭제 (여행 목적지 지정 확정 후)")
    @Operation(summary = "특정 일차 여행 목적지 삭제 (여행 목적지 지정 확정 후)", description = "특정 일차 여행 목적지를 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 목적지 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deletePlace(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId,

            @Parameter(description = "목적지 ID")
            @PathVariable String placeId
    );
}
