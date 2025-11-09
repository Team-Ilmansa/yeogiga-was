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
                                                  "type": "TRANSPORT",
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
    
    @TrackApi(description = "정산 내역 전체 조회")
    @Operation(summary = "정산 내역 전체 조회", description = "정산 내역 전체 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 내역 전체 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                 "code": 200,
                                                 "message": "요청이 성공하였습니다.",
                                                 "data": {
                                                     "2025-09-08": [
                                                         {
                                                             "id": 4,
                                                             "name": "숙소",
                                                             "totalPrice": 30000,
                                                             "date": "2025-09-08",
                                                             "type": "LODGING",
                                                             "payerId": 16,
                                                             "isCompleted": true,
                                                             "payers": [
                                                                 {
                                                                     "id": 7,
                                                                     "userId": 15,
                                                                     "nickname": "nick15",
                                                                     "imageUrl": "https://image.com/15",
                                                                     "price": 10000,
                                                                     "isCompleted": true
                                                                 },
                                                                 {
                                                                     "id": 8,
                                                                     "userId": 16,
                                                                     "nickname": "nick16",
                                                                     "imageUrl": null,
                                                                     "price": 20000,
                                                                     "isCompleted": true
                                                                 }
                                                             ]
                                                         },
                                                         {
                                                             "id": 5,
                                                             "name": "2팀 저녁 숙소",
                                                             "totalPrice": 50000,
                                                             "date": "2025-09-08",
                                                             "type": "LODGING",
                                                             "payerId": 16,
                                                             "isCompleted": false,
                                                             "payers": [
                                                                 {
                                                                     "id": 9,
                                                                     "userId": 15,
                                                                     "nickname": "nick15",
                                                                     "imageUrl": "https://image.com/15",
                                                                     "price": 10000,
                                                                     "isCompleted": false
                                                                 },
                                                                 {
                                                                     "id": 10,
                                                                     "userId": 16,
                                                                     "nickname": "nick16",
                                                                     "imageUrl": null,
                                                                     "price": 20000,
                                                                     "isCompleted": true
                                                                 }
                                                             ]
                                                         }
                                                     ],
                                                     "2025-09-07": [
                                                         {
                                                             "id": 1,
                                                             "name": "주유소",
                                                             "totalPrice": 50000,
                                                             "date": "2025-09-07",
                                                             "type": "TRANSPORT",
                                                             "payerId": 16,
                                                             "isCompleted": false,
                                                             "payers": [
                                                                 {
                                                                     "id": 1,
                                                                     "userId": 15,
                                                                     "nickname": "nick15",
                                                                     "imageUrl": "https://image.com/15",
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
                                                         },
                                                         {
                                                             "id": 2,
                                                             "name": "점심",
                                                             "totalPrice": 30000,
                                                             "date": "2025-09-07",
                                                             "type": "RESTAURANT",
                                                             "payerId": 16,
                                                             "isCompleted": true,
                                                             "payers": [
                                                                 {
                                                                     "id": 3,
                                                                     "userId": 15,
                                                                     "nickname": "nick15",
                                                                     "imageUrl": "https://image.com/15",
                                                                     "price": 10000,
                                                                     "isCompleted": true
                                                                 },
                                                                 {
                                                                     "id": 4,
                                                                     "userId": 16,
                                                                     "nickname": "nick16",
                                                                     "imageUrl": null,
                                                                     "price": 20000,
                                                                     "isCompleted": true
                                                                 }
                                                             ]
                                                         },
                                                         {
                                                             "id": 3,
                                                             "name": "저녁",
                                                             "totalPrice": 30000,
                                                             "date": "2025-09-07",
                                                             "type": "RESTAURANT",
                                                             "payerId": 16,
                                                             "isCompleted": true,
                                                             "payers": [
                                                                 {
                                                                     "id": 5,
                                                                     "userId": 15,
                                                                     "nickname": "nick15",
                                                                     "imageUrl": "https://image.com/15",
                                                                     "price": 10000,
                                                                     "isCompleted": true
                                                                 },
                                                                 {
                                                                     "id": 6,
                                                                     "userId": 16,
                                                                     "nickname": "nick16",
                                                                     "imageUrl": null,
                                                                     "price": 20000,
                                                                     "isCompleted": true
                                                                 }
                                                             ]
                                                         }
                                                     ]
                                                 }
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "정산 내역 전체 조회 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 멤버가 아닌 경우", value = """
                                             {
                                                   "code": "T102",
                                                   "message": "해당 여행의 멤버가 아닙니다."
                                               }
                                    """)
                    }))
    })
    ResponseEntity<?> getAllSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            
            @Parameter(description = "여행 ID")
            @PathVariable(name ="tripId") Long tripId
    );
    
    @TrackApi(description = "정산 내역 수정")
    @Operation(summary = "정산 내역 수정", description = "정산 내역 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 내역 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                     "code": 200,
                                                     "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "정산 내역 수정 실패",
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
            @ApiResponse(responseCode = "403", description = "정샌 내역 수정 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "요청자가 정산자가 아닌 경우",value = """
                                             {
                                                  "code": "S002",
                                                  "message": "정산자 생성자가 아닙니다."
                                              }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "정샌 내역 수정 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 여행",value = """
                                             {
                                                 "code": "T006",
                                                 "message": "해당 여행이 존재하지 않습니다."
                                             }
                                    """),
                            @ExampleObject(name = "존재하지 않는 정산 내역",value = """
                                             {
                                                  "code": "S001",
                                                  "message": "존재하지 않는 정산 내역 입니다."
                                              }
                                    """)
                    }))
    })
    ResponseEntity<?> updateSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            
            @Parameter(description = "여행 ID")
            @PathVariable(name = "tripId") Long tripId,
            
            @Parameter(description = "정산 내역 ID")
            @PathVariable(name = "settlementId") Long settlementId,
            
            @Valid @RequestBody SettlementRequest.SettlementDto settlement
    );
    
    @TrackApi(description = "정산 완료 여부 갱신")
    @Operation(summary = "정산 완료 여부 갱신", description = "정산 완료 여부 갱신 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 완료 여부 갱신 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                             {
                                                     "code": 200,
                                                     "message": "요청이 성공하였습니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "정산 완료 여부 갱신 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효성 검사 실패", value = """
                                             {
                                                 "code": "G002",
                                                 "errors": {
                                                     "payInfos": "정산 완료 목록은 최소 1개 이상이어야 합니다."
                                                 }
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "정산 완료 여부 갱신 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "요청자가 정산자가 아닌 경우",value = """
                                             {
                                                  "code": "S002",
                                                  "message": "정산 생성자가 아닙니다."
                                              }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "정산 완료 여부 갱신 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하지 않는 정산 내역",value = """
                                             {
                                                  "code": "S001",
                                                  "message": "존재하지 않는 정산 내역 입니다."
                                              }
                                    """),
                            @ExampleObject(name = "존재하지 않는 분담 내역",value = """
                                             {
                                                 "code": "S003",
                                                 "message": "존재하지 않는 분담 내역입니다."
                                             }
                                    """)
                    }))
    })
    ResponseEntity<?> completeSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            
            @Parameter(description = "정산 내역 ID")
            @PathVariable(name = "settlementId") Long settlementId,
            @Valid @RequestBody SettlementRequest.PayInfoCompletionListDto request
    );
    
    @TrackApi(description = "정산 내역 삭제")
    @Operation(summary = "정산 내역 삭제", description = "정산 내역 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정산 내역 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                            {
                                                "code": 200,
                                                "message": "요청이 성공하였습니다."
                                            }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "정산 내역 삭제 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "여행 멤버가 아닌 경우", value = """
                                             {
                                                   "code": "T102",
                                                   "message": "해당 여행의 멤버가 아닙니다."
                                               }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "정산 내역 삭제 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "요청자가 정산 생성자가 아닌 경우", value = """
                                             {
                                                 "code": "S002",
                                                 "message": "정산 생성자가 아닙니다."
                                             }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "정산 내역 삭제 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "정산 내역 미존재",value = """
                                             {
                                                  "code": "S001",
                                                  "message": "존재하지 않는 정산 내역 입니다."
                                              }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            
            @Parameter(description = "여행 ID")
            @PathVariable(name = "tripId") Long tripId,
            
            @Parameter(description = "정산 내역 ID")
            @PathVariable(name = "settlementId") Long settlementId
    );
}
