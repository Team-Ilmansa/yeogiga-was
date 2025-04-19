package kr.co.yeogiga.domain.trip.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Trip ErrorCode: Txxx
 */
@RequiredArgsConstructor
public enum TripErrorType implements BaseErrorType {

    INVALID_PLACE(HttpStatus.BAD_REQUEST, "T001", "지원하지 않는 카테고리입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getCode() {
        return "";
    }

    @Override
    public String getMessage() {
        return "";
    }
}
