package kr.co.yeogiga.domain.settlement.dto;

public record PayInfoDto(
        Long id,
        Long userId,
        String nickname,
        String imageUrl,
        Long price,
        boolean isCompleted
) {
}
