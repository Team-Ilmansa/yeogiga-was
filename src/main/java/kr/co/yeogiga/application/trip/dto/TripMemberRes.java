package kr.co.yeogiga.application.trip.dto;

import kr.co.yeogiga.domain.user.entity.User;
import lombok.Builder;

public class TripMemberRes {

    @Builder
    public record MemberInfo(
            Long userId,
            String nickname,
            String imageUrl
    ) {
        public static MemberInfo fromEntity(User user) {
            return MemberInfo.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .imageUrl(user.getImageUrl())
                    .build();
        }
    }
}
