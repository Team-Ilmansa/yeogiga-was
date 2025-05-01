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

@ApiGroup(value = "[여행 목적지 API]")
@Tag(name = "[여행 목적지 API]", description = "여행 목적지 관련 API")
public interface TripPlaceApi {

    @TrackApi(description = "여행 목적지 확정")
    @Operation(summary = "여행 목적지 확정", description = "여행 목적지를 확정하는 API입니다.")
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
    ResponseEntity<?> completeTrip(@PathVariable Long tripId,
                                   @RequestBody TripPlaceDto.CompleteRequest request);

    @TrackApi(description = "여행 목적지 추가")
    @Operation(summary = "여행 목적지 추가", description = "여행 목적지를 추가하는 API입니다.")
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
    ResponseEntity<?> addNewPlace(@PathVariable String tripId,
                                  @PathVariable String tripDayPlaceId,
                                  @RequestBody TripPlaceDto.InsertRequest insertRequest);

    @TrackApi(description = "여행 목적지 순서 변경")
    @Operation(summary = "여행 목적지 순서 변경", description = "여행 목적지 순서를 변경하는 API입니다.")
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
    ResponseEntity<?> reorderPlaces(@PathVariable String tripId,
                                    @PathVariable String tripDayPlaceId,
                                    @RequestBody TripPlaceDto.ReorderRequest reorderRequest);

    @TrackApi(description = "여행 목적지 삭제")
    @Operation(summary = "여행 목적지 삭제", description = "여행 목적지를 삭제하는 API입니다.")
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
    ResponseEntity<?> deletePlace(@PathVariable String tripId,
                                  @PathVariable String tripDayPlaceId,
                                  @PathVariable String placeId);
}
