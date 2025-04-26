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
    NOT_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "A003", "인증이 필요합니다."),
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