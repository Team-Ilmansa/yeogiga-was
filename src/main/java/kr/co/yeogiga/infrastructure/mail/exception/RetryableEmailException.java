package kr.co.yeogiga.infrastructure.mail.exception;

public class RetryableEmailException extends EmailException {
    public RetryableEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public boolean isRetryable() {
        return true;
    }
}
