package kr.co.yeogiga.presentation.image.api;

import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.yeogiga.application.image.dto.ImageUrlDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiGroup(value = "[이미지 API]")
@Tag(name = "[이미지 API]", description = "이미지 관련 API")
public interface ImageApi {

    @TrackApi(description = "이미지 업로드")
    @Operation(summary = "이미지 업로드", description = "이미지 업로드하는 API입니다.")
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
    ResponseEntity<?> uploadImages(@RequestPart(value = "images", required = false) List<MultipartFile> images,
                                   @PathVariable Long tripId,
                                   @PathVariable String tripDayPlaceId);

    @TrackApi(description = "이미지 삭제")
    @Operation(summary = "이미지 삭제", description = "이미지 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "code": 200,
                                            "message": "요청이 성공하였습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteImage(@RequestBody ImageUrlDto imageUrlDto);
}
