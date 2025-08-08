package kr.co.yeogiga.common.response.error.type;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Common ErrorCode: Gxxx
 */
@RequiredArgsConstructor
public enum CommonErrorType implements BaseErrorType {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G001", "서버 내부 에러입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "G002", "유효성 검증에 실패하였습니다."),
    PATH_VARIABLE_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "G003", "지원하지 않는 Path Variable 값입니다."),
    TIME_SHOULD_NOT_BEFORE_NOW(HttpStatus.BAD_REQUEST, "G004", "요청 시각은 현재 시각 이전이 될 수 없습니다."),
    QUERY_STRING_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "G005", "지원하지 않는 Query String 값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return status;
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