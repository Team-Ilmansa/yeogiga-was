package kr.co.yeogiga.infrastructure.mail.exception;

import kr.co.yeogiga.infrastructure.event.exception.RetryableException;

public class RetryableEmailException extends RetryableException {
    public RetryableEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public boolean isRetryable() {
        return true;
    }
}
