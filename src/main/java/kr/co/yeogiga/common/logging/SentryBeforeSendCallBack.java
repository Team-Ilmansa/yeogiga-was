package kr.co.yeogiga.common.logging;

import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import io.sentry.protocol.SentryException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SentryBeforeSendCallBack implements SentryOptions.BeforeSendCallback {
    
    /**
     * Sentry로 이벤트를 발송하기 전 처리를 수행하는 메서드
     * - {@code MethodArgumentNotValidException} 발생 시 이슈 메시지 설정
     * - 동일 타입 예외(이슈) 스택 그룹핑을 방지하기 위한 Fingerprint 설정
     *
     * @param sentryEvent   Sentry 이벤트
     * @param hint          힌트
     * @return              Sentry 이벤트
     */
    @Override
    public SentryEvent execute(SentryEvent sentryEvent, Hint hint) {
        handleMethodArgumentNotValidException(sentryEvent);
        sentryEvent.setFingerprints(List.of(UUID.randomUUID().toString()));
        return sentryEvent;
    }
    
    /**
     * {@code MethodArgumentNotValidException} 에러 필드 처리 메서드
     *
     * @param sentryEvent   Sentry 이벤트
     */
    private void handleMethodArgumentNotValidException(SentryEvent sentryEvent) {
        if (sentryEvent.getThrowable() != null) {
            if (sentryEvent.getThrowable() instanceof MethodArgumentNotValidException e) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
                
                List<SentryException> exceptions = sentryEvent.getExceptions();
                
                if (exceptions != null && !exceptions.isEmpty()) {
                    SentryException exception = exceptions.get(exceptions.size() - 1);
                    exception.setValue(errors.toString());
                }
            }
        }
    }
}
