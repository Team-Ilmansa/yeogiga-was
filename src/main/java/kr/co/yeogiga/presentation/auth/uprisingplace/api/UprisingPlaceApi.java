package kr.co.yeogiga.presentation.auth.uprisingplace.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@ApiGroup(value = "[급상승 여행지 API]")
@Tag(name = "[급상승 여행지 API]", description = "급상승 여행지 관련 API")
public interface UprisingPlaceApi {
    
    @TrackApi(description = "급상승 여행지 전체 조회")
    @Operation(summary = "급상승 여행지 전체 조회", description = "급상승 여행지 전체 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "급상승 여행지 전체 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(description = "급상승 인기 여행지 10곳 정보를 반환", value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다.",
                                            "data": [
                                                {
                                                    "id": 1,
                                                    "name": "말티재",
                                                    "address": "충청북도 보인군 속리산면 갈목리 산19-6",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/1"
                                                },
                                                {
                                                    "id": 2,
                                                    "name": "관방제림",
                                                    "address": "전라남도 담양군 담양읍 객사7길 37",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/2"
                                                },
                                                {
                                                    "id": 3,
                                                    "name": "불국사",
                                                    "address": "경상북도 경주시 불국로 385",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/3"
                                                },
                                                {
                                                    "id": 4,
                                                    "name": "부석사",
                                                    "address": "경상북도 영주시 부석면 부석사로 345",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/4"
                                                },
                                                {
                                                    "id": 5,
                                                    "name": "반계리 은행나무",
                                                    "address": "강원특별자치도원주시 문막음 반계리 1496-1",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/5"
                                                },
                                                {
                                                    "id": 6,
                                                    "name": "팔공산 분수대광장",
                                                    "address": "대구광역시 동구 용수동 27-5",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/6"
                                                },
                                                {
                                                    "id": 7,
                                                    "name": "삼성궁",
                                                    "address": "경상남도 하동군 청암면 삼성궁길 13",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/7"
                                                },
                                                {
                                                    "id": 8,
                                                    "name": "다대포 해수욕장",
                                                    "address": "부산광역시 사하구 몰운대1길 14",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/8"
                                                },
                                                {
                                                    "id": 9,
                                                    "name": "월정리 해수욕장",
                                                    "address": "제주특별자치도 제주시 조천읍 월정1길 35",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/9"
                                                },
                                                {
                                                    "id": 10,
                                                    "name": "호국의병의숲 친수공원",
                                                    "address": "경남 의령군 지정면 성산리 672",
                                                    "placeCategory": "TOURISM",
                                                    "url": "https://image.com/10"
                                                }
                                            ]
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getAllUprisingPlaces();
}
