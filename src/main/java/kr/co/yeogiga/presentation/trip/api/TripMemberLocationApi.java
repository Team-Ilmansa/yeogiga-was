package kr.co.yeogiga.presentation.trip.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.yeogiga.application.trip.dto.TripMemberLocationDto;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[여행 멤버 위치 API]")
@Tag(name = "[여행 멤버 위치 API]", description = "여행 멤버 위치 관련 API")
public interface TripMemberLocationApi {
    
    @TrackApi(description = "여행 멤버 위치 저장")
    @Operation(summary = "여행 멤버 위치 저장", description = "여행 멤버 위치 저장 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "여행 멤버 위치 저장 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 201,
                                                 "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 멤버 위치 저장 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "위도, 경도 범위 초과", value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "latitude": "위도는 90도보다 클 수 없습니다.",
                                                     "longitude": "경도는 180도보다 클 수 없습니다."
                                                 }
                                             }
                                    """),
                            @ExampleObject(name = "위도, 경도가 null인 경우", value = """
                                             {
                                                  "code": "G002",
                                                  "errors": {
                                                      "latitude": "위도는 필수 입력값입니다.",
                                                      "longitude": "경도는 필수 입력값입니다."
                                                  }
                                              }
                                    """),
                            @ExampleObject(name = "여행이 존재하지 않거나 멤버가 아닌 경우", value = """
                                             {
                                                 "code": "T102",
                                                 "message": "해당 여행의 멤버가 아닙니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> saveLocation(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,
            
            @Valid @RequestBody TripMemberLocationDto.Request request
    );
    
    @TrackApi(description = "여행 멤버 위치 조회")
    @Operation(summary = "여행 멤버 위치 조회", description = "여행 멤버 위치 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행 멤버 위치 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "저장된 위치가 없는 경우", value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다.",
                                                 "data": []
                                             }
                                    """),
                            @ExampleObject(name = "저장된 위치가 있는 경우", value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다.",
                                                 "data": [
                                                     {
                                                         "latitude": 30.2,
                                                         "longitude": 120.1,
                                                         "userId": 2,
                                                         "nickname": "nick2",
                                                         "imageUrl": null
                                                     },
                                                     {
                                                         "latitude": 30.2,
                                                         "longitude": 120.1,
                                                         "userId": 1,
                                                         "nickname": "nick",
                                                         "imageUrl": null
                                                     }
                                                 ]
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "여행 멤버 위치 조회 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 미존재 또는 여행 멤버가 아닌 경우", value = """
                                             {
                                                 "code": "T102",
                                                 "message": "해당 여행의 멤버가 아닙니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> getLocations(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId
    );
}
