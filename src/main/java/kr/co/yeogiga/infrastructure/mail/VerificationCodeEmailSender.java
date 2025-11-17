package kr.co.yeogiga.infrastructure.mail;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class VerificationCodeEmailSender extends AbstractEmailSender {
    public VerificationCodeEmailSender(JavaEmailSender javaEmailSender, TemplateEngine templateEngine) {
        super(javaEmailSender, templateEngine);
    }
    
    private final String SUBJECT = "[여기가] 이메일 인증 코드";
    private final String CODE_VARIABLE_NAME = "code";
    private final String EMAIL_VERIFICATION_CODE_TEMPLATE_NAME = "email-verification-code-template";
    
    public void send(String email, String code) {
        Context context = new Context();
        context.setVariable(CODE_VARIABLE_NAME, code);
        
        super.send(email, SUBJECT, EMAIL_VERIFICATION_CODE_TEMPLATE_NAME, context);
    }
}
