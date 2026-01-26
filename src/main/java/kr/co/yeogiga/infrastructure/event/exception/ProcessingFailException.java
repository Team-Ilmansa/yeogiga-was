package kr.co.yeogiga.infrastructure.event.exception;

public class ProcessingFailException extends RuntimeException {
    public ProcessingFailException(String message) {
        super(message);
    }
}
