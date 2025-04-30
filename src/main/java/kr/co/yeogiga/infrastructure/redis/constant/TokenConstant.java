package kr.co.yeogiga.infrastructure.redis.constant;

public final class TokenConstant {
    private TokenConstant() { }

    public static final String REFRESH_TOKEN_PREFIX = "refresh_token:%d";

    public static String parseRefreshTokenKey(Long userId) {
        return String.format(REFRESH_TOKEN_PREFIX, userId);
    }
}