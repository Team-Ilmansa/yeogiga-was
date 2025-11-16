package kr.co.yeogiga.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class PasswordResetEmailSender {
    @Value("${mail.password-reset-url}")
    private String passwordResetUrl;
    
    private final JavaEmailSender javaEmailSender;
    private final TemplateEngine templateEngine;
    
    private final String SUBJECT = "[여기가] 비밀번호 초기화";
    private final String PASSWORD_RESET_URL_VARIABLE_NAME = "password_reset_url";
    private final String PASSWORD_RESET_TEMPLATE_NAME = "password-reset-template";
    private final String LOGO_NAME = "logo";
    private final String LOGO_LOCATION = "static/images/logo.png";
    
    public void send(String email, String code) {
        Context context = new Context();
        context.setVariable(PASSWORD_RESET_URL_VARIABLE_NAME, generateUrl(code));
        
        String html = templateEngine.process(PASSWORD_RESET_TEMPLATE_NAME, context);
        Content content = new Content(LOGO_NAME, LOGO_LOCATION);
        
        javaEmailSender.send(email, SUBJECT, html, content);
    }
    
    private String generateUrl(String code) {
        return passwordResetUrl + "?code=" + code;
    }
}
