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

@ApiGroup(value = "[여행 목적지 확정 API]")
@Tag(name = "[여행 목적지 확정 API]", description = "여행 목적지 확정 관련 API")
public interface TripPlaceSavingApi {

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
}
