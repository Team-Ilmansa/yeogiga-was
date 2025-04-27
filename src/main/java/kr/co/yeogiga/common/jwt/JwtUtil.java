package kr.co.yeogiga.common.jwt;

import static kr.co.yeogiga.application.auth.constant.AuthConstants.TOKEN_TYPE;

public class JwtUtil {
    public static String resolveToken(String token) {
        return token.replace(TOKEN_TYPE, "").trim();
    }
}
