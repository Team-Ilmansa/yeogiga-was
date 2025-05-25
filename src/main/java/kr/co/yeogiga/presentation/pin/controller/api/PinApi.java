package kr.co.yeogiga.presentation.pin.controller.api;

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
import kr.co.yeogiga.application.pin.dto.PinReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[집결지 핀(PIN) API]")
@Tag(name = "[집결지 핀(PIN) API]", description = "집결지 핀(PIN) API")
public interface PinApi {

    @TrackApi(description = "집결지 핀 생성")
    @Operation(summary = "집결지 핀 생성", description = "집결지 핀 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "집결지 핀 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 201,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "집결지 핀 생성 실패",
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
                            @ExampleObject(name = "null 값인 경우", value = """
                                        {
                                              "code": "G002",
                                              "errors": {
                                                  "latitude": "위도는 필수 입력값입니다.",
                                                  "place": "집결지 장소는 필수 입력값입니다.",
                                                  "time": "집결 시간은 필수 입력값입니다.",
                                                  "longitude": "경도는 필수 입력값입니다."
                                              }
                                          }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "집결지 핀 생성 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 미존재", value = """
                                        {
                                              "code": "T006",
                                              "message": "해당 여행이 존재하지 않습니다."
                                          }
                                    """)
                    })),
    })
    ResponseEntity<?> createPin(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Valid @RequestBody PinReq.Creation request
    );
}
