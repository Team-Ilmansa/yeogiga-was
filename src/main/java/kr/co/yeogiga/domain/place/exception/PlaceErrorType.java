package kr.co.yeogiga.domain.place.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Place ErrorCode: Pxxx
 */
@RequiredArgsConstructor
public enum PlaceErrorType implements BaseErrorType {
    NOT_VALID_PLACE_QUERY(HttpStatus.BAD_REQUEST, "P000", "장소는 필수 입력값입니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    
    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
    
    @Override
    public String getCode() {
        return this.code;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
}
