package kr.co.yeogiga.presentation.route.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.route.dto.RouteReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[여행 루트(GPS) 정보 API]")
@Tag(name = "[여행 루트(GPS) 정보 API]", description = "여행 루트(GPS) 정보 관련 API")
public interface RouteApi {

    @TrackApi(description = "방장의 위치 저장 (주기적 요청)")
    @Operation(summary = "방장의 위치 저장 (주기적 요청)", description = "방장의 위치를 저장하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "방장 위치 저장 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 201,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> storeLeaderRoute(
            @PathVariable Long tripId,
            @PathVariable int day,
            @RequestBody RouteReq.Request routeReq
    );
}
