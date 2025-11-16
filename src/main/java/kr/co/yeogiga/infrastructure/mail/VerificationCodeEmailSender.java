package kr.co.yeogiga.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class VerificationCodeEmailSender {
    private final JavaEmailSender javaEmailSender;
    private final TemplateEngine templateEngine;
    
    private final String SUBJECT = "[여기가] 이메일 인증 코드";
    private final String CODE_VARIABLE_NAME = "code";
    private final String EMAIL_VERIFICATION_CODE_TEMPLATE_NAME = "email-verification-code-template";
    private final String LOGO_NAME = "logo";
    private final String LOGO_LOCATION = "static/images/logo.png";
    
    public void send(String email, String code) {
        Context context = new Context();
        context.setVariable(CODE_VARIABLE_NAME, code);
        
        String html = templateEngine.process(EMAIL_VERIFICATION_CODE_TEMPLATE_NAME, context);
        Content content = new Content(LOGO_NAME, LOGO_LOCATION);
        
        javaEmailSender.send(email, SUBJECT, html, content);
    }
}
