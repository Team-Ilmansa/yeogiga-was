package kr.co.yeogiga.domain.auth.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Auth ErrorCode: Axxx
 */
@RequiredArgsConstructor
public enum AuthErrorType implements BaseErrorType {
    FORBIDDEN(HttpStatus.FORBIDDEN, "A000", "접근 권한이 없습니다."),
    UN_REGISTERED_USER(HttpStatus.FORBIDDEN, "A001", "회원가입이 필요한 사용자 입니다."),
    NOT_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "A003", "인증에 실패하였습니다. 토큰을 확인해주세요."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "잘못된 토큰입니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "A006", "토큰 서명이 잘못되었습니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "A007", "토큰 인증 과정 중 에러가 발생하였습니다."),
    ;


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