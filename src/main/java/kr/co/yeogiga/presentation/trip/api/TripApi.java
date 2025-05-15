package kr.co.yeogiga.presentation.trip.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.trip.dto.TripReq;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[여행 API]")
@Tag(name = "[여행 API]", description = "여행 관련 API")
public interface TripApi {

    @TrackApi(description = "여행 생성")
    @Operation(summary = "여행 생성", description = "여행 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 201,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 생성 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "최대 글자수 초과", value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "title": "제목은 최대 20글자까지 가능합니다."
                                                 }
                                             }
                                    """),
                            @ExampleObject(name = "필수 입력값 미입력", value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "title": "제목은 필수 입력값입니다."
                                                 }
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> createTrip(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TripReq.Creation request
    );
}
