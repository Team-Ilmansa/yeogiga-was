package kr.co.yeogiga.infrastructure.mail.exception;

import kr.co.yeogiga.infrastructure.event.exception.RetryableException;

public class FatalEmailException extends RetryableException {
    public FatalEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public boolean isRetryable() {
        return false;
    }
}
