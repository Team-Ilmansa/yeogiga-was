package kr.co.yeogiga.presentation.settlement.api;

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
import kr.co.yeogiga.application.settlement.dto.SettlementRequest;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@ApiGroup(value = "[정산 API]")
@Tag(name = "[정산 API]", description = "정산 관련 API")
public interface SettlementApi {
    
    @TrackApi(description = "정산 내역 생성")
    @Operation(summary = "정산 내역 생성", description = "정산 내역 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "정산 내역 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                     "code": 201,
                                                     "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "정산 내역 생성 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검사 실패", value = """
                                             {
                                                  "code": "G002",
                                                  "errors": {
                                                      "payers[1].price": "최소 금액은 0원입니다.",
                                                      "totalPrice": "최소 금액은 0원입니다.",
                                                      "name": "이름은 필수 입력값입니다."
                                                  }
                                              }
                                    """),
                            @ExampleObject(name = "정산 내역 총합 금액 불일치", value = """
                                             {
                                                   "code": "S000",
                                                   "message": "정산 내역 금액 총합이 일치하지 않습니다."
                                             }
                                    """),
                            @ExampleObject(name = "여행 멤버가 아닌 정산자가 존재하는 경우", value = """
                                             {
                                                  "code": "T105",
                                                  "message": "여행 멤버가 아닌 사용자가 존재합니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "정샌 내역 생성 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 여행",value = """
                                             {
                                                 "code": "T006",
                                                 "message": "해당 여행이 존재하지 않습니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> createSettlement(
            @Parameter(description = "여행 ID") @PathVariable(name = "tripId") Long tripId,
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody SettlementRequest.SettlementDto settlement
    );
    
    @TrackApi(description = "정산 내역 조회")
    @Operation(summary = "정산 내역 조회", description = "정산 내역 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 내역 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                  "id": 1,
                                                  "name": "주유소",
                                                  "totalPrice": 50000,
                                                  "date": "2025-09-07",
                                                  "type": "TRANSPORTATION",
                                                  "payerId": 16,
                                                  "isCompleted": false,
                                                  "payers": [
                                                      {
                                                          "id": 1,
                                                          "userId": 15,
                                                          "nickname": "nick15",
                                                          "imageUrl": "https://image.com/1",
                                                          "price": 10000,
                                                          "isCompleted": false
                                                      },
                                                      {
                                                          "id": 2,
                                                          "userId": 16,
                                                          "nickname": "nick16",
                                                          "imageUrl": null,
                                                          "price": 20000,
                                                          "isCompleted": true
                                                      }
                                                  ]
                                              }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "정산 내역 조회 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 멤버가 아닌 경우", value = """
                                             {
                                                   "code": "T102",
                                                   "message": "해당 여행의 멤버가 아닙니다."
                                               }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "정산 내역 조회 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "정산 내역 미존재",value = """
                                             {
                                                  "code": "S001",
                                                  "message": "존재하지 않는 정산 내역 입니다."
                                              }
                                    """)
                    }))
    })
    ResponseEntity<?> getSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            
            @Parameter(description = "여행 ID")
            @PathVariable(name ="tripId") Long tripId,
            
            @Parameter(description = "정산 내역 ID")
            @PathVariable(name = "settlementId") Long settlementId
    );
}
