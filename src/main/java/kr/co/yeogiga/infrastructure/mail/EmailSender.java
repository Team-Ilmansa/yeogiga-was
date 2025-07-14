package kr.co.yeogiga.infrastructure.mail;

import org.springframework.beans.factory.annotation.Value;

public abstract class EmailSender {
    
    @Value("${spring.mail.username}")
    protected String username;
    
    public abstract void send(String to, String subject, String content);
}
