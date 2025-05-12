package kr.co.yeogiga.application.auth.type;

import java.util.Objects;

public enum LoginType {
    NORMAL, SOCIAL;

    public static LoginType from(String password) {
        return Objects.isNull(password) ? SOCIAL : NORMAL;
    }
}
