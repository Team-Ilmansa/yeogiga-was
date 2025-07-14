package kr.co.yeogiga.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationCodeEmailSender {
    private final JavaEmailSender javaEmailSender;
    private final String SUBJECT = "[여기가] 이메일 인증 코드";
    // TODO: thymeleaf 템플릿 엔진을 통한 이메일 인증 코드 메일 템플릿 추가 시 삭제 필요
    private final String CONTENT = """
                <h1>여기가 이메일 인증 코드</h1>
                <h2>%s</h2>
                <p>3분 안에 인증을 진행하세요.</p>
            """;
    
    public void send(String email, String code) {
        javaEmailSender.send(email, SUBJECT, CONTENT.formatted(code));
    }
}
