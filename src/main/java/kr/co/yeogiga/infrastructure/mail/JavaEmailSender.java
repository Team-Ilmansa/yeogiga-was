package kr.co.yeogiga.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaEmailSender extends EmailSender {
    private final JavaMailSender javaMailSender;
    
    @Async
    @Override
    public void send(String to, String subject, String html, Content... contents) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(username);
            helper.setText(html, true);
            
            for (Content content : contents) {
                helper.addInline(content.contentId(), new ClassPathResource(content.contentLocation()));
            }
            
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("[ERROR] Failed to send mail - to: {} | subject: {} | message: {}", to, subject, e.getMessage());
        }
    }
}
