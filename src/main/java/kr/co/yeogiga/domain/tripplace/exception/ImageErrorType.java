package kr.co.yeogiga.domain.tripplace.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Image ErrorCode: Ixxx
 */
@RequiredArgsConstructor
public enum ImageErrorType implements BaseErrorType {

    NOT_FOUND_TEMP_IMAGE_STORE(HttpStatus.NOT_FOUND, "I000", "임시 저장된 이미지가 존재하지 않습니다.");
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
