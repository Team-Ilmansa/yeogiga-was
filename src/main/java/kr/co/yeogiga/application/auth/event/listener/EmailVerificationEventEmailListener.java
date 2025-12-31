package kr.co.yeogiga.application.auth.event.listener;

import kr.co.yeogiga.application.auth.event.EmailVerificationEvent;
import kr.co.yeogiga.application.event.listener.DomainEventListener;
import kr.co.yeogiga.infrastructure.mail.VerificationCodeEmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static kr.co.yeogiga.infrastructure.config.AsyncConfig.EMAIL_TASK_EXECUTOR;

@Component
@RequiredArgsConstructor
public class EmailVerificationEventEmailListener extends DomainEventListener<EmailVerificationEvent> {
    private final VerificationCodeEmailSender verificationCodeEmailSender;
    
    @Override
    @Async(value = EMAIL_TASK_EXECUTOR)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(EmailVerificationEvent event) {
        verificationCodeEmailSender.send(event.getEmail(), event.getCode());
        // TODO: Transactional Outbox Pattern 적용 시, 이벤트 상태 변경 로직 추가 필요
    }
}
