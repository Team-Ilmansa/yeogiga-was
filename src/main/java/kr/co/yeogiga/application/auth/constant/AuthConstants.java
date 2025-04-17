package kr.co.yeogiga.application.auth.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthConstants {
    REFRESH_TOKEN_PREFIX("refreshToken");

    private final String value;

}