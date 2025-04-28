package kr.co.yeogiga.presentation.trip.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.trip.dto.TripPlaceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@ApiGroup(value = "[여행 생성 단계에서의 목적지 API]")
@Tag(name = "[여행 생성 단계에서의 목적지 API]", description = "여행 목적지 관련 API")
public interface TripPlaceEditingApi {

    @TrackApi(description = "목적지 추가")
    @Operation(summary = "목적지 추가", description = "목적지 추가하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "목적지 추가 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
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
                    })),
    })
    ResponseEntity<?> addPlace(@PathVariable Long tripId,
                               @PathVariable int day,
                               @RequestBody TripPlaceDto.Request request);

    @TrackApi(description = "일자별 목적지 조회")
    @Operation(summary = "일자별 목적지 조회", description = "일자별 목적지 조회하는 API입니다.")
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
                                                    "placeCategory": "RESTAURANT"
                                                },
                                                {
                                                    "id": "place-id2",
                                                    "name": "목적지2",
                                                    "latitude": 132.33,
                                                    "longitude": 287.123,
                                                    "placeCategory": "RESTAURANT"
                                                }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getPlaces(@PathVariable Long tripId, @PathVariable int day);

    @TrackApi(description = "목적지 수정")
    @Operation(summary = "목적지 수정", description = "목적지 수정하는 API입니다.")
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
    ResponseEntity<?> updatePlaces(@PathVariable Long tripId,
                                   @PathVariable int day,
                                   @RequestBody List<TripPlaceDto.Request> requests);

    @TrackApi(description = "목적지 삭제")
    @Operation(summary = "목적지 삭제", description = "목적지 삭제하는 API입니다.")
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
    ResponseEntity<?> deletePlace(@PathVariable Long tripId,
                                  @PathVariable int day,
                                  @PathVariable String placeId);
}
