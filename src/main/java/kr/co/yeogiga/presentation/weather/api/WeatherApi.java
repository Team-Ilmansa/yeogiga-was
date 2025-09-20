package kr.co.yeogiga.presentation.weather.api;

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
import org.springframework.web.bind.annotation.RequestParam;

@ApiGroup(value = "[날씨 관련 API]")
@Tag(name = "[날씨 관련 API]", description = "날씨 관련 API")
public interface WeatherApi {

    @TrackApi(description = "날씨 조회")
    @Operation(summary = "날씨 조회", description = "날씨 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "날씨 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다.",
                                                 "data": [
                                                     {
                                                         "category": "TMP",
                                                         "fcstValue": "17"
                                                     },
                                                     {
                                                         "category": "UUU",
                                                         "fcstValue": "-0.7"
                                                     },
                                                     {
                                                         "category": "VVV",
                                                         "fcstValue": "-1.9"
                                                     }
                                                 ]
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> getWeather(
            @Parameter(description = "현재 격자 공간 x")
            @RequestParam int nx,

            @Parameter(description = "현재 격자 공간 y")
            @RequestParam int ny
    );
}
