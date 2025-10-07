package kr.co.yeogiga.presentation.settlement.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.settlement.dto.SettlementRequest;
import kr.co.yeogiga.application.settlement.service.SettlementCommandService;
import kr.co.yeogiga.application.settlement.service.SettlementQueryService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.presentation.settlement.api.SettlementApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class SettlementController implements SettlementApi {
    private final SettlementCommandService settlementCommandService;
    private final SettlementQueryService settlementQueryService;
    
    @Override
    @PostMapping("/{tripId}/settlements")
    public ResponseEntity<?> createSettlement(
            @PathVariable(name = "tripId") Long tripId,
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody SettlementRequest.SettlementDto settlement
    ) {
        settlementCommandService.createSettlement(tripId, userDetails.getUserId(), settlement);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.created());
    }
    
    @Override
    @GetMapping("/{tripId}/settlements/{settlementId}")
    public ResponseEntity<?> getSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable(name ="tripId") Long tripId,
            @PathVariable(name = "settlementId") Long settlementId
    ) {
        return ResponseEntity
                .ok(SuccessResponse.from(settlementQueryService.getSettlement(tripId, userDetails.getUserId(), settlementId)));
    }
    
    @Override
    @GetMapping("/{tripId}/settlements")
    public ResponseEntity<?> getAllSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable(name = "tripId") Long tripId
    ) {
        return ResponseEntity
                .ok(SuccessResponse.from(settlementQueryService.getAllSettlement(tripId, userDetails.getUserId())));
    }
    
    @PutMapping("/{tripId}/settlements/{settlementId}")
    public ResponseEntity<?> updateSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable(name = "tripId") Long tripId,
            @PathVariable(name = "settlementId") Long settlementId,
            @RequestBody SettlementRequest.SettlementDto settlement
    ) {
        settlementCommandService.updateSettlement(tripId, userDetails.getUserId(), settlementId, settlement);
        
        return ResponseEntity.ok(SuccessResponse.ok());
    }
    
    @Override
    @DeleteMapping("/{tripId}/settlements/{settlementId}")
    public ResponseEntity<?> deleteSettlement(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable(name = "tripId") Long tripId,
            @PathVariable(name = "settlementId") Long settlementId
    ) {
        settlementCommandService.deleteSettlement(tripId, userDetails.getUserId(), settlementId);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
