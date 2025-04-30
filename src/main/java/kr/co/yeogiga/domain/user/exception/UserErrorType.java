package kr.co.yeogiga.domain.user.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * User ErrorCode: Uxxx
 */
@RequiredArgsConstructor
public enum UserErrorType implements BaseErrorType {
    NOT_FOUND(HttpStatus.NOT_FOUND, "U000", "존재하지 않는 사용자입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
