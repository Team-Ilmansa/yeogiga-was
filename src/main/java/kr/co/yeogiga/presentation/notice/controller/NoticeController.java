package kr.co.yeogiga.presentation.notice.controller;

import jakarta.validation.Valid;
import kr.co.yeogiga.application.notice.dto.NoticeReq;
import kr.co.yeogiga.application.notice.service.NoticeCommandService;
import kr.co.yeogiga.application.notice.service.NoticeQueryService;
import kr.co.yeogiga.common.response.success.SuccessResponse;
import kr.co.yeogiga.common.security.auth.CustomUserDetailsImpl;
import kr.co.yeogiga.presentation.notice.api.NoticeApi;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip")
public class NoticeController implements NoticeApi {
    private final NoticeCommandService noticeCommandService;
    private final NoticeQueryService noticeQueryService;
    
    @Override
    @PostMapping("/{tripId}/notices")
    public ResponseEntity<?> createNotice(
            @PathVariable(name = "tripId") Long tripId,
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @Valid @RequestBody NoticeReq.Creation request
    ) {
        noticeCommandService.createNotice(userDetails.getUserId(), tripId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.created());
    }
    
    @Override
    @GetMapping("/{tripId}/notices")
    public ResponseEntity<?> getNotices(
            @PathVariable(name = "tripId") Long tripId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok()
                .body(SuccessResponse.from(noticeQueryService.getAllNotices(tripId, pageable)));
    }
    
    @PutMapping("/{tripId}/notices/{noticeId}")
    public ResponseEntity<?> updateNotice(
            @AuthenticationPrincipal CustomUserDetailsImpl userDetails,
            @PathVariable(name = "noticeId") Long noticeId,
            @Valid @RequestBody NoticeReq.Creation request
    ) {
        noticeCommandService.updateNotice(noticeId, userDetails.getUserId(), request);
        return ResponseEntity.ok(SuccessResponse.ok());
    }
}
