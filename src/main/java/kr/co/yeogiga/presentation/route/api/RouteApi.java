package kr.co.yeogiga.presentation.route.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@ApiGroup(value = "[여행 루트(GPS) 정보 API]")
@Tag(name = "[여행 루트(GPS) 정보 API]", description = "여행 루트(GPS) 정보 관련 API")
public interface RouteApi {

    @TrackApi(description = "여행 전체 루트(이동 경로) 조회 - 방장의 루트")
    @Operation(summary = "여행 전체 루트(이동 경로) 조회 - 방장의 루트", description = "여행 전체 루트(이동 경로) 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "방장 위치 저장 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": [
                                                {
                                                    "day": 1,
                                                    "routes": [
                                                        {
                                                            "latitude": 0.0,
                                                            "longitude": 1.1,
                                                            "time": "2025-05-16T01:16:44"
                                                        },
                                                        {
                                                            "latitude": 0.5,
                                                            "longitude": 1.5,
                                                            "time": "2025-05-16T01:16:47"
                                                        }
                                                    ]
                                                },
                                                {
                                                    "day": 2,
                                                    "routes": [
                                                        {
                                                            "latitude": 2.0,
                                                            "longitude": 3.0,
                                                            "time": "2025-05-16T01:16:44"
                                                        },
                                                        {
                                                            "latitude": 2.5,
                                                            "longitude": 3.5,
                                                            "time": "2025-05-16T01:16:47"
                                                        }
                                                    ]
                                                }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getTripRoutes(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );
}
