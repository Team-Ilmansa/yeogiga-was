package kr.co.yeogiga.common.util;

import java.security.SecureRandom;

public class VerificationCodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * 6자리 난수를 생성하는 메서드
     * - 범위: 100,000 ~ 999,999
     *
     * @return 6자리 난수
     */
    public static String generate() {
        return String.valueOf(secureRandom.nextInt(900000) + 100000);
    }
}
