package kr.co.yeogiga.common.jwt.dto;

import lombok.Builder;

@Builder
public record JwtClaims(
        String nickname,
        Long userId
) {
}