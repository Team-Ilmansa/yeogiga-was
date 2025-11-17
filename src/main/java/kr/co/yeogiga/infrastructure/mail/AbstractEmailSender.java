package kr.co.yeogiga.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
public abstract class AbstractEmailSender {
    private final JavaEmailSender javaEmailSender;
    private final TemplateEngine templateEngine;
    
    private final String LOGO_NAME = "logo";
    private final String LOGO_LOCATION = "static/images/logo.png";
    
    protected void send(String email, String subject, String template, Context context) {
        String html = templateEngine.process(template, context);
        
        Content content = new Content(LOGO_NAME, LOGO_LOCATION);
        
        javaEmailSender.send(email, subject, html, content);
    }
}
