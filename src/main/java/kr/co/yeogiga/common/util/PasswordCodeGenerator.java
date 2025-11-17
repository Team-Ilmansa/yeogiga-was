package kr.co.yeogiga.common.util;

import java.util.UUID;

public class PasswordCodeGenerator {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
