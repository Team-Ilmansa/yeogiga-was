package kr.co.yeogiga.common.response.error.type;

import org.springframework.http.HttpStatus;

public interface BaseErrorType {
    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
