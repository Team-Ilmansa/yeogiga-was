package kr.co.yeogiga.presentation.notice.api;


import api.link.checker.annotation.ApiGroup;
import api.link.checker.annotation.TrackApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
            @PathVariable(name = "tripId") Long tripId,
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody NoticeReq.Creation request
    );
    
    @TrackApi(description = "전체 공지사항 조회")
    @Operation(summary = "전체 공지사항 조회", description = "전체 공지사항 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 공지사항 조회 성공. 기본 페이지 별 공지사항 갯수(size): 10",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                  "code": 200,
                                                  "message": "요청이 성공하였습니다.",
                                                  "data": {
                                                      "content": [
                                                          {
                                                              "id": 24,
                                                              "title": "준비물 챙기세요.",
                                                              "description": "수건, 양말...",
                                                              "createdAt": "2025-07-27T11:56:34.546218",
                                                              "authorId": 13,
                                                              "nickname": "nick13",
                                                              "imageUrl": "https://image.com/image.png"
                                                          },
                                                          {
                                                              "id": 23,
                                                              "title": "여행 전 주의사항",
                                                              "description": "여행 전 해당 내용 인지하시길 바랍니다.",
                                                              "createdAt": "2025-07-27T11:56:29.82594",
                                                              "authorId": 13,
                                                              "nickname": "nick13",
                                                              "imageUrl": "https://image.com/image.png"
                                                          },
                                                          {
                                                              "id": 22,
                                                              "title": "여행 경비 안내",
                                                              "description": "여행 경비는 인당...",
                                                              "createdAt": "2025-07-27T11:56:27.579067",
                                                              "authorId": 13,
                                                              "nickname": "nick13",
                                                              "imageUrl": "https://image.com/image.png"
                                                          }
                                                      ],
                                                      "page": {
                                                          "size": 10,
                                                          "number": 0,
                                                          "totalElements": 12,
                                                          "totalPages": 4
                                                      }
                                                  }
                                              }
                                    """)
                    }))
    })
    @Parameter(name = "page", description = "페이지네이션", example = "page=0&size=10&sort=createdAt,desc", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
    ResponseEntity<?> getNotices(
            @Parameter(description = "여행 ID")
            @PathVariable(name = "tripId") Long tripId,
            
            @Parameter(example = "page=0&size=10&sort=createdAt,desc", hidden = true)
            @PageableDefault(size = 10) Pageable pageable
    );
    
    @TrackApi(description = "공지사항 수정")
    @Operation(summary = "공지사항 수정", description = "공지사항 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                  "code": 200,
                                                  "message": "요청이 성공하였습니다."
                                              }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "공지사항 수정 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검증 실패", value = """
                                             {
                                                   "code": "G002",
                                                   "errors": {
                                                       "description": "내용은 필수 입력값입니다.",
                                                       "title": "제목은 필수 입력값입니다."
                                                   }
                                               }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "공지사항 수정 실패 - 공지사항의 작성자가 아닌 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "공지사항 작성자가 아닌 경우", value = """
                                             {
                                                    "code": "N001",
                                                    "message": "공지사항의 작성자가 아닙니다."
                                                }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "공지사항 수정 실패 - 존재하지 않는 공지사항",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "공지사항이 존재하지 않는 경우", value = """
                                             {
                                                     "code": "N000",
                                                     "message": "존재하지 않는 공지사항입니다."
                                                 }
                                    """)
                    }))
    })
    ResponseEntity<?> updateNotice(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable(name = "noticeId") Long noticeId,
            @Valid @RequestBody NoticeReq.Creation request
    );
    
    @TrackApi(description = "공지사항 삭제")
    @Operation(summary = "공지사항 삭제", description = "공지사항 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                  "code": 200,
                                                  "message": "요청이 성공하였습니다."
                                              }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "공지사항 삭제 실패 - 공지사항의 작성자가 아닌 경우",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "공지사항 작성자가 아닌 경우", value = """
                                             {
                                                    "code": "N001",
                                                    "message": "공지사항의 작성자가 아닙니다."
                                                }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "공지사항 삭제 실패 - 존재하지 않는 공지사항",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "공지사항이 존재하지 않는 경우", value = """
                                             {
                                                     "code": "N000",
                                                     "message": "존재하지 않는 공지사항입니다."
                                                 }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteNotice(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable(name = "noticeId") Long noticeId
    );
}
