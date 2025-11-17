package kr.co.yeogiga.infrastructure.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class PasswordResetEmailSender extends AbstractEmailSender {
    @Value("${mail.password-reset-url}")
    private String passwordResetUrl;
    
    public PasswordResetEmailSender(
            JavaEmailSender javaEmailSender,
            TemplateEngine templateEngine,
            @Value("${mail.password-reset-url}")
            String passwordResetUrl
    ) {
        super(javaEmailSender, templateEngine);
        this.passwordResetUrl = passwordResetUrl;
    }
    
    private final String SUBJECT = "[여기가] 비밀번호 초기화";
    private final String PASSWORD_RESET_URL_VARIABLE_NAME = "password_reset_url";
    private final String PASSWORD_RESET_TEMPLATE_NAME = "password-reset-template";
    
    public void send(String email, String code) {
        Context context = new Context();
        context.setVariable(PASSWORD_RESET_URL_VARIABLE_NAME, generateUrl(code));
        
        super.send(email, SUBJECT, PASSWORD_RESET_TEMPLATE_NAME, context);
    }
    
    private String generateUrl(String code) {
        return passwordResetUrl + "?code=" + code;
    }
}
