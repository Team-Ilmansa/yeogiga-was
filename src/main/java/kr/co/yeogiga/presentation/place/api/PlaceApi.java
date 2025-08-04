package kr.co.yeogiga.presentation.place.api;

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

@ApiGroup(value = "[장소 검색 API]")
@Tag(name = "[장소 검색 API]", description = "장소 검색 관련 API")
public interface PlaceApi {
    
    @TrackApi(description = "장소 검색")
    @Operation(summary = "장소 검색", description = "키워드를 통해 장소를 검색하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장소 검색 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(description = "네이버 지역 검색 api 제한으로 인해 최대 5개까지 출력 가능", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다.",
                                             "data": [
                                                 {
                                                     "title": "경복궁",
                                                     "link": "https://royal.khs.go.kr/gbg",
                                                     "description": "",
                                                     "telephone": "",
                                                     "address": "서울특별시 종로구 세종로 1-1 경복궁",
                                                     "roadAddress": "서울특별시 종로구 사직로 161 경복궁",
                                                     "longitude": 126.9770162,
                                                     "latitude": 37.5788408
                                                 },
                                                 {
                                                     "title": "경복궁 한옥마을점",
                                                     "link": "http://www.entas.co.kr/",
                                                     "description": "",
                                                     "telephone": "",
                                                     "address": "인천광역시 연수구 송도동 24-17",
                                                     "roadAddress": "인천광역시 연수구 테크노파크로 180",
                                                     "longitude": 126.6388695,
                                                     "latitude": 37.3907929
                                                 },
                                                 {
                                                     "title": "경복궁 판교점",
                                                     "link": "http://www.entas.co.kr/",
                                                     "description": "",
                                                     "telephone": "",
                                                     "address": "경기도 성남시 분당구 삼평동 741 2층",
                                                     "roadAddress": "경기도 성남시 분당구 대왕판교로606번길 58 2층",
                                                     "longitude": 127.1132503,
                                                     "latitude": 37.395166
                                                 },
                                                 {
                                                     "title": "경복궁 방이점",
                                                     "link": "http://www.entas.co.kr/",
                                                     "description": "",
                                                     "telephone": "",
                                                     "address": "서울특별시 송파구 방이동 35",
                                                     "roadAddress": "서울특별시 송파구 올림픽로 348",
                                                     "longitude": 127.1090835,
                                                     "latitude": 37.5159841
                                                 },
                                                 {
                                                     "title": "경복궁 대구점",
                                                     "link": "http://www.entas.co.kr/",
                                                     "description": "",
                                                     "telephone": "",
                                                     "address": "대구광역시 수성구 두산동 793",
                                                     "roadAddress": "대구광역시 수성구 용학로 134",
                                                     "longitude": 128.6233411,
                                                     "latitude": 35.8234245
                                                 }
                                             ]
                                         }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "장소 검색 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(description = "장소 키워드 유효성 검증 실패", value = """
                                        {
                                              "code": "P000",
                                              "message": "장소는 필수 입력값입니다."
                                          }
                                    """)
                    }))
    })
    ResponseEntity<?> searchPlace(
            @Parameter(description = "장소 키워드", example = "경복궁")
            @RequestParam(name = "place") String place
    );
    
}
