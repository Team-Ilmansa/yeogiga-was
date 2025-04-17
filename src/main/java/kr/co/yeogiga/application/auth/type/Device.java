package kr.co.yeogiga.application.auth.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Device {
    MOBILE("MOBILE"),
    WEB("WEB");

    private final String value;
}