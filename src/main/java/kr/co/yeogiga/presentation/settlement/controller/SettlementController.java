package kr.co.yeogiga.presentation.settlement.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.settlement.dto.SettlementRequest;
import kr.co.yeogiga.application.settlement.service.SettlementCommandService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.presentation.settlement.api.SettlementApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
public class SettlementController implements SettlementApi {
    private final SettlementCommandService settlementCommandService;
    
    @Override
    @PostMapping("/{tripId}")
    public ResponseEntity<?> createSettlement(
            @PathVariable(name = "tripId") Long tripId,
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody SettlementRequest.SettlementDto settlement
    ) {
        settlementCommandService.createSettlement(tripId, userDetails.getUserId(), settlement);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.created());
    }
}
