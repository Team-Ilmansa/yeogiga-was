package kr.co.yeogiga.domain.trip.exception;

import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * TripMember ErrorCode: T1xx
 */
@RequiredArgsConstructor
public enum TripMemberErrorType implements BaseErrorType {

    ALREADY_EXISTS(HttpStatus.CONFLICT, "T100", "이미 여행에 참가 중인 사용자입니다.");

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
