package kr.co.yeogiga.infrastructure.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaEmailSender extends EmailSender {
    private final JavaMailSender javaMailSender;
    
    @Async
    @Override
    public void send(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper
                    = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            
            messageHelper.setSubject(subject);
            messageHelper.setTo(to);
            messageHelper.setFrom(username);
            messageHelper.setText(content, true);
            
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("[ERROR] Failed to send mail - to: {} | subject: {} | message: {}", to, subject, e.getMessage());
        }
        
    }
}
