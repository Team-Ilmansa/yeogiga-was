package kr.co.yeogiga.common.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final BaseErrorType errorType;
    private final Object data;
    
    public CustomException(BaseErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = null;
    }
    
    public CustomException(BaseErrorType errorType, Object data) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = data;
    }
}