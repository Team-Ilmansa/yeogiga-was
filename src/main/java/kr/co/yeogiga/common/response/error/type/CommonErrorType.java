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

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "G004", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "G005", "잘못된 토큰입니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "G006", "토큰 서명이 잘못되었습니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "G007", "토큰 인증 과정 중 에러가 발생하였습니다."),
    ;

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