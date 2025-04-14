package kr.co.yeogiga.domain.oauth.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OAuthErrorType implements BaseErrorType {

    OAUTH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "O001", "소셜 로그인 과정에서 에러가 발생하였습니다. 관리자에게 문의하세요.");

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