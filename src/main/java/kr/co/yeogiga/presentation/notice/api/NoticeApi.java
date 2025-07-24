package kr.co.yeogiga.presentation.notice.api;


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
import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.common.security.auth.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[공지사항 API]")
@Tag(name = "[공지사항 API]", description = "공지사항 관련 API")
public interface NoticeApi {
    
    @TrackApi(description = "공지사항 생성")
    @Operation(summary = "공지사항 생성", description = "공지사항 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공지사항 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                     "code": 201,
                                                     "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "공지사항 생성 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검사 실패",value = """
                                             {
                                                     "code":"G002",
                                                     "errors": {
                                                          "description": "내용은 필수 입력값입니다.",
                                                          "title": "제목은 필수 입력값입니다."
                                                     }
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> createNotice(
            @Parameter(description = "여행 ID")
            @PathVariable(name = "tripId") Long tripId,
            
            @AuthenticationPrincipal CustomUserDetails userDetails,
            
            @Valid @RequestBody NoticeReq.Creation request
    );
}
