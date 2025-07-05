package kr.co.yeogiga.application.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VerificationCodeGeneratorTest {
    private final VerificationCodeGenerator generator = new VerificationCodeGenerator();
    
    @Test
    @DisplayName("인증 코드는 6자리 난수")
    void verifyVerificationCode() {
        String code = generator.generate();
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}");
    }
    
    @RepeatedTest(100)
    @DisplayName("인증 코드는 항상 6자리 난수를 보장")
    void verifyRandomCode() {
        String code = generator.generate();
        int value = Integer.parseInt(code);
        assertThat(value).isBetween(100_000, 999_999);
    }
    
}
