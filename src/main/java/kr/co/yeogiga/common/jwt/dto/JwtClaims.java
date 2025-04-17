package kr.co.yeogiga.common.jwt.dto;

import lombok.Builder;

@Builder
public record JwtClaims(
        String username,
        String nickname,
        Long userId
) {
}