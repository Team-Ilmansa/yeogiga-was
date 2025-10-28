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
import kr.co.yeogiga.application.tripplace.dto.TripPlaceReqLegacy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[여행 생성 단계에서의 목적지 API]")
@Tag(name = "[여행 생성 단계에서의 목적지 API]", description = "여행 목적지 관련 API")
public interface TripPlaceEditingApi {

    @TrackApi(description = "일차에 목적지 넣기")
    @Operation(summary = "일차에 목적지 넣기", description = "일차에 목적지 넣는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "일차에 목적지 넣기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 201,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 추가 되어 있는 목적지",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": "T002",
                                            "message": "이미 추가된 장소입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> assignPlaceToDay(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "배정하고자 하는 여행 일차")
            @PathVariable int day,

            @RequestBody TripPlaceReqLegacy.Request place
    );

    @TrackApi(description = "일차에 지정한 목적지 조회")
    @Operation(summary = "일차에 지정한 목적지 조회", description = "일차에 지정한 목적지 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일자별 목적지 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": [
                                                {
                                                    "id": "place-id1",
                                                    "name": "목적지1",
                                                    "latitude": 32.33,
                                                    "longitude": 87.123,
                                                    "placeType": "관광지"
                                                },
                                                {
                                                    "id": "place-id2",
                                                    "name": "목적지2",
                                                    "latitude": 132.33,
                                                    "longitude": 287.123,
                                                    "placeType": "식당"
                                                }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getAssignedPlaces(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "조회하고자 하는 일차")
            @PathVariable int day
    );

    @TrackApi(description = "일차별 목적지의 순서를 수정 (이 과정에서 목적지 삭제도 가능)")
    @Operation(summary = "일차별 목적지의 순서를 수정 (이 과정에서 목적지 삭제도 가능)", description = "일차별 목적지의 순서를 수정하는 API입니다." +
            "<br/> 예를 들어, 기존 순서가 (a, b, c, d, e)이고, 순서를 (b, c, a, d, e)로 바꾼다면 바뀐 순서에 대한 목적지 id를 List형식으로 바꿔 보내면 됩니다." +
            "<br/> 삭제도 가능하다는 말은, (a, b, c, d, e)에서 (b, c, e, d)로 바꿔서 보낸다면 a는 자동으로 삭제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목적지 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> reorderPlaces(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "수정하고자 하는 일차")
            @PathVariable int day,

            @Parameter(description = "변경된 순서로의 목적지 ID 리스트")
            @RequestBody TripPlaceReqLegacy.ReorderRequest request
    );

    @TrackApi(description = "일차별 목적지의 순서 추천 알고리즘")
    @Operation(summary = "일차별 목적지의 순서 추천 알고리즘", description = "일차별 목적지의 순서 추천 알고리즘 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목적지 순서 추천 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> sortDayTripPlaces(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "추천받고자 하는 일차")
            @PathVariable int day
    );

    @TrackApi(description = "일차에 지정된 목적지 삭제")
    @Operation(summary = "일차에 지정된 목적지 삭제", description = "일차에 지정된 목적지 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목적지 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteAssignedPlace(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "수행할 일차")
            @PathVariable int day,

            @Parameter(description = "목적지 ID")
            @PathVariable String placeId
    );
}
