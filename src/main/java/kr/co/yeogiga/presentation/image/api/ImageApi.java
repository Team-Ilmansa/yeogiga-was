package kr.co.yeogiga.presentation.image.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.image.dto.ImageDeleteDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiGroup(value = "[이미지 매칭 전 API]")
@Tag(name = "[이미지 매칭 전 API]", description = "이미지 매칭 전 관련 API")
public interface ImageApi {

    @TrackApi(description = "이미지 업로드 (정렬하기 전 단계이므로, 일차별 임시 저장소로 이동)")
    @Operation(summary = "이미지 업로드 (정렬하기 전 단계이므로, 일차별 임시 저장소로 이동)", description = "이미지 업로드하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "이미지 업로드 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 201,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> uploadImages(
            @Parameter(description = "업로드할 이미지 파일 목록")
            @RequestPart(value = "images", required = false) List<MultipartFile> images,

            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId
    );

    @TrackApi(description = "일차별 임시 저장된 이미지 목적지에 매칭 (정렬 버튼 클릭)")
    @Operation(summary = "일차별 임시 저장된 이미지 목적지에 매칭 (정렬 버튼 클릭)", description = "일차별 임시 저장된 이미지 목적지에 매칭하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 목적지 매핑 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "찾을 수 없는 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "임시 저장된 이미지가 없음", value = """
                                        {
                                             "code": "I000",
                                             "message": "임시 저장된 이미지가 존재하지 않습니다."
                                        }
                                    """),
                            @ExampleObject(name = "여행 일차 존재하지 않음", value = """
                                        {
                                            "code": "T003",
                                            "message": "해당 여행 일차 정보가 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> assignImages(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId
    );

    @TrackApi(description = "여행 일차별 임시 저장 이미지 조회")
    @Operation(summary = "여행 일차별 임시 저장 이미지 조회", description = "여행 일차별 임시 저장 이미지 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 저장된 이미지 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "임시 저장 이미지 내역 있음", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다.",
                                             "data": [
                                                 {
                                                     "id": "image1-id",
                                                     "url": "https://image1.com"
                                                 },
                                                 {
                                                     "id": "image2-id",
                                                     "url": "https://image2.com"
                                                 }
                                             ]
                                        }
                                    """),
                            @ExampleObject(name = "임시 저장 이미지 내역 없음 (빈 리스트 반환)", value = """
                                        {
                                             "code": 200,
                                             "message": "요청이 성공하였습니다.",
                                             "data": []
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getTempImagesInfo(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId
    );

    @TrackApi(description = "일차별 이미지 매칭 전 임시 저장된 이미지 삭제")
    @Operation(summary = "일차별 이미지 매칭 전 임시 저장된 이미지 삭제", description = "일차별 이미지 매칭 전 임시 저장된 이미지 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "임시 이미지 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> removeTempImages(
            @Parameter(description = "여행 ID")
            @PathVariable Long tripId,

            @Parameter(description = "여행 일차 ID")
            @PathVariable String tripDayPlaceId,

            @Parameter(description = "임시 이미지 삭제 정보")
            @RequestBody ImageDeleteDto deleteDto
    );
}
