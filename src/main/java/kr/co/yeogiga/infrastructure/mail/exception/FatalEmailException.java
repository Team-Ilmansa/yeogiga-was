package kr.co.yeogiga.infrastructure.mail.exception;

public class FatalEmailException extends EmailException {
    public FatalEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public boolean isRetryable() {
        return false;
    }
}
