package kr.co.yeogiga.infrastructure.mail.exception;

public abstract class EmailException extends RuntimeException {
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 재시도 가능 여부를 반환하는 메서드
     *
     * @return 재시도 가능 여부
     */
    public abstract boolean isRetryable();
}
